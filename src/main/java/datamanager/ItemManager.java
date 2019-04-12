package datamanager;

import datastruct.Item;
import datastruct.feature.Location;
import datastruct.feature.Point;
import io.sql.ItemDBHandler;
import multithread.ReloadProcess;
import multithread.Reloadable;
import nlp.Dictionary;
import nlp.DictionaryManager;
import org.apache.log4j.Logger;
import datamanager.helper.DistanceCalculator;

import java.util.*;

public class ItemManager implements Reloadable {
    private static final Logger LOGGER = Logger.getLogger(ItemManager.class);
    private static final int MAX_ITEM_MAP_SIZE = 200000;    // Giới hạn số lượng item được giữ trong itemMap, khi vượt qua giới hạn này thì sẽ phải xoá bợt item cũ đi.
    private static final int NUM_ITEM_KEEP = 180000;        // Số lượng item còn giữ lại sau mỗi lần xoá trên luôn < MAX_ITEM_MAP_SIZE 1 khoảng để tránh phải thực hiện thao tác xoá thường xuyên
    private static final int TIME_RELOAD = 1000;            // Thời gian dãn cách mỗi lần load dữ liệu
    private int numDate = 0;                // Số ngày item hết hạn vẫn được giữ
    private final int MAX_CANDIDATE_SIZE = 2500;        // Kích thước tối đa của itemAvailable

    /**
     * A Set of item ids, keep ids of items is available to be recommended.
     * Only items be loaded from reload method will keep there.
     */
    private Set<Long> itemAvailable;

    /**
     * Map item id to item, It can't also keep items that don't in itemAvailable,
     * These items exist in this map maybe be get from user history
     */
    private Map<Long, Item> itemMap;

    private DistanceCalculator disCalculator;

    /**
     * Singleton
     */
    private static ItemManager instance = null;

    private ItemManager() {
        itemAvailable = new HashSet<>();
        itemMap = new HashMap<>();
        reload();
        System.out.println("Finish create item manager. Available itemMap: " + itemMap.size());
        disCalculator = DistanceCalculator.getInstance();
    }

    private ItemManager(int numDateKeepItem) {
        numDate = numDateKeepItem;
        itemAvailable = new HashSet<>();
        itemMap = new HashMap<>();
        disCalculator = DistanceCalculator.getInstance();
        reload();
        System.out.println("Finish create item manager. Available itemMap: " + itemMap.size());
    }

    public static void init(int numDateKeepItem) {
        instance = new ItemManager(numDateKeepItem);
    }

    public static ItemManager getInstance() {
        if (instance == null) {
            synchronized (ItemManager.class) {
                if (instance == null)
                    instance = new ItemManager();
            }
        }
        return instance;
    }

    public Set<Long> getItemIds() {
        return itemAvailable;
    }

    public Item getItem(Long id) {
        return itemMap.get(id);
    }

    /**
     * Get items, if having item not existing in itemMap, get that item from database.
     * This new item won't add to available ids
     *
     * @param ids
     * @return
     */
    public List<Item> getFetchItem(List<Long> ids) {
        List<Item> result = new ArrayList<>();
        List<Long> toQueryIds = new ArrayList<>();
        for (Long id : ids) {
            if (itemMap.containsKey(id))
                result.add(itemMap.get(id));
            else {
                toQueryIds.add(id);
            }
        }

        if (toQueryIds.size() > 0) {
            Dictionary dict = DictionaryManager.getInstance().getDictionary();
            ItemDBHandler itemDBHandler = new ItemDBHandler();
            List<Item> items = itemDBHandler.getItemById(toQueryIds);

            if (items.isEmpty()) {
                return new ArrayList<>();
            }
            for (Item item : items) {
                if (item.getContent().getValue().trim().equals("")) {
                    continue;
                }
                String[] w = item.getContent().getValue().split("\\s+");
                List<String> words = new ArrayList<>();
                for (String s : w) {
                    if (!s.equals("")) {
                        words.add(s);
                    }
                }
                item.setTfidf(dict.docToTfidfNormalized(words));
                result.add(item);
            }
            if (!result.isEmpty()) {
                Map<Long, Item> tempItemMap = new HashMap<>(itemMap);
                for (Item item : result) {
                    tempItemMap.put(item.getId(), item);
                }
                itemMap = tempItemMap;
            }
        }
        return result;
    }

    /**
     * Get items, if having item not existing in itemMap, get that item from database.
     * This new item won't add to available ids
     *
     * @param ids
     * @return
     */
    public List<Item> getWithoutFetchItem(List<Long> ids) {
        List<Item> result = new ArrayList<>();
        for (Long id : ids) {
            if (itemMap.containsKey(id))
                result.add(itemMap.get(id));
        }
        return result;
    }

    public void addItem(Item item) {
        itemMap.put(item.getId(), item);
    }

    @Override
    public void reload() {
        LOGGER.info("Update item manager");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -numDate);
        ItemDBHandler itemDBHandler = new ItemDBHandler();
        Set<Long> itemIds = new HashSet<>(itemDBHandler.getIdAvailable(calendar.getTime()));
        Set<Long> removedIds = new HashSet<>();
//        Map<Long, Item> new
        for (Long id : itemAvailable) {
            if (!itemIds.contains(id)) {
                removedIds.add(id);
            }
        }
        itemAvailable.removeAll(removedIds);
        List<Long> newIds = new ArrayList<>();
        for (Long id : itemIds) {
            if (!itemMap.containsKey(id)) {
                newIds.add(id);
            }
        }

        /**
         * tempItem help to update without change itemMap
         */
        Map<Long, Item> tempItemMap = new HashMap<>(itemMap);
        Set<Long> tempItemAvailable = new HashSet<>(itemAvailable);
        List<Item> itemData = itemDBHandler.getItemByIdsMiniBatch(newIds);
        Dictionary dict = DictionaryManager.getInstance().getDictionary();
        for (Item item : itemData) {
            Long id = item.getId();
            if (item.getContent().getValue().trim().equals("")) {
                continue;
            } else {
                newIds.remove(id);
            }
            String[] w = item.getContent().getValue().split("\\s+");
            List<String> words = new ArrayList<>();
            for (String s : w) {
                if (!s.equals("")) {
                    words.add(s);
                }
            }
            item.setTfidf(dict.docToTfidfNormalized(words));
            tempItemMap.put(id, item);
            tempItemAvailable.add(id);
        }

        if (tempItemMap.size() > MAX_ITEM_MAP_SIZE) {
            int numRemoved = tempItemMap.size() - NUM_ITEM_KEEP;
            List<Long> idMaybeRemove = new ArrayList<>();
            for (Long id : tempItemMap.keySet()) {
                if (!itemIds.contains(id)) {
                    idMaybeRemove.add(id);
                }
            }
            idMaybeRemove.sort(Long::compareTo);
            for (int i = 0; i < numRemoved && i < idMaybeRemove.size(); i++) {
                tempItemMap.remove(idMaybeRemove.get(i));
            }
        }
        itemMap = tempItemMap;
        itemAvailable = tempItemAvailable;
        LOGGER.info("Finish update item manager. Num current item: " + itemMap.size());
    }

    @Deprecated
    public void reloadOldVersion() {
        LOGGER.info("Update item manager");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -numDate);
        ItemDBHandler itemDBHandler = new ItemDBHandler();
        Set<Long> itemIds = new HashSet<>(itemDBHandler.getIdAvailable(calendar.getTime()));
        Set<Long> removedIds = new HashSet<>();
        for (Long id : itemAvailable) {
            if (!itemIds.contains(id)) {
                removedIds.add(id);
            }
        }
        itemAvailable.removeAll(removedIds);
        List<Long> newIds = new ArrayList<>();
        for (Long id : itemIds) {
            if (!itemMap.containsKey(id)) {
                newIds.add(id);
            }
        }

        List<Item> itemData = itemDBHandler.getItemByIdsMiniBatch(newIds);
        Dictionary dict = DictionaryManager.getInstance().getDictionary();
        for (Item item : itemData) {
            Long id = item.getId();
            if (item.getContent().getValue().trim().equals("")) {
                continue;
            }
            String[] w = item.getContent().getValue().split("\\s+");
            List<String> words = new ArrayList<>();
            for (String s : w) {
                if (!s.equals("")) {
                    words.add(s);
                }
            }
            item.setTfidf(dict.docToTfidfNormalized(words));
            itemMap.put(id, item);
            itemAvailable.add(id);
        }

        if (itemMap.size() > MAX_ITEM_MAP_SIZE) {
            int numRemoved = itemMap.size() - NUM_ITEM_KEEP;
            List<Long> idMaybeRemove = new ArrayList<>();
            for (Long id : itemMap.keySet()) {
                if (!itemIds.contains(id)) {
                    idMaybeRemove.add(id);
                }
            }
            idMaybeRemove.sort(Long::compareTo);
            for (int i = 0; i < numRemoved && i < idMaybeRemove.size(); i++) {
                itemMap.remove(idMaybeRemove.get(i));
            }
        }
        LOGGER.info("Finish update item manager. Num current item: " + itemMap.size());
    }

    /**
     * Start reload Thread
     */
    public void startAutoReload() {
        ReloadProcess reloadProcess = new ReloadProcess(this, TIME_RELOAD);
        reloadProcess.start();
    }

    /**
     * Get item available list
     *
     * @return
     */
    public List<Item> getAvailableItems() {
        List<Item> available = new ArrayList<>();
        for (Long itemId : itemAvailable) {
            available.add(itemMap.get(itemId));
        }
        return available;
    }

    /**
     * Using the item provided as center context to get candidate items.
     */
    public List<Item> filteringCandidates(List<Location> locs, Item currentItem) {
        List<Item> candidates = new ArrayList<>();
        Set<Integer> limitedLocs = new HashSet<>();
        for (Location l : locs)
            limitedLocs.add(l.getAddress().getDistrictId());
        int category = currentItem.getCategory();
        int sellType = currentItem.getSellType();

        for (Long id : itemAvailable) {
            Item item = itemMap.get(id);
            boolean sellTypeCondition = (item.getCategory() == category && item.getSellType() == sellType);

            if (!sellTypeCondition)
                continue;

            // 2 item are in the same district
            if (limitedLocs.contains(item.getLocation().getAddress().getDistrictId()))
                candidates.add(item);
            else if (item.getLocation().getAddress().getCityId() == currentItem.getLocation().getAddress().getCityId()) {
                boolean near = isLocal(currentItem, item);
                if (near)
                    candidates.add(item);
            }

            // stop if fetching candidate item reach maximum size
            if (candidates.size() >= MAX_CANDIDATE_SIZE)
                break;
        }

        return candidates;
    }

    /**
     * without item specific, just filtering candidate as item which has same value with filter params
     * @param : an empty listing means accepting all value.
     */
    public List<Item> filteringCandidatesWithoutItemContext(List<Integer> sellTypes,
                                                            List<Integer> categories,
                                                            List<Integer> cities) {
        List<Item> candidates = new ArrayList<>();

        for (long itemId : itemAvailable) {
            Item item = itemMap.get(itemId);
            boolean isRelevant = (cities.isEmpty() || cities.contains(item.getLocation().getAddress().getCityId()))
                                && (categories.isEmpty() || categories.contains(item.getCategory()))
                                && (sellTypes.isEmpty() || sellTypes.contains(item.getSellType()));

            if (!isRelevant)
                continue;
            candidates.add(item);

            // stop if fetching candidate item reach maximum size
            if (candidates.size() >= MAX_CANDIDATE_SIZE)
                break;
        }

        return candidates;
    }

    /**
     * determine if 2 items are local area
     */
    public boolean isLocal(Item item1, Item item2) {
        Point p1 = item1.getLocation().getValue().getRight();
        Point p2 = item2.getLocation().getValue().getRight();

        // if coordinate is unknown
        if (p1 == null || p2 == null)
            return false;

        double dis = disCalculator.getDistance(item1, item2);

        return dis <= DistanceCalculator.LOCAL_DISTANCE_KM;
    }
}
