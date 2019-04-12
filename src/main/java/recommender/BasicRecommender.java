package recommender;

import datamanager.ItemManager;
import datamanager.UserLogManager;
import datastruct.HistoryCell;
import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.Location;
import scoring.*;

import java.util.*;

/**
 * Basic recommender, version 1.0s
 */
public class BasicRecommender extends RecommendAbstract {
    /**
     * Instance object, this class can create only 1 instance
     */
    private static BasicRecommender instance = null;

    private BasicRecommender(ItemManager itemManager, UserLogManager logManager) {
        super(itemManager, logManager);
    }

    /**
     * Init instance object
     *
     * @param itemManager item manager object
     * @param logManager  user log manager object
     */
    public static void init(ItemManager itemManager, UserLogManager logManager) {
        if (instance == null) {
            instance = new BasicRecommender(itemManager, logManager);
        }
    }

    /**
     * Get instance object
     * @return
     */
    public static BasicRecommender getInstance() {
        return instance;
    }

    /**
     * Recommend for user with current item id
     * Main recommend method, will be used for product
     *
     * @param uid
     * @param itemId
     * @return
     */
    @Override
    public List<Pair<Item, Double>> getRecommend(String uid, long itemId) {
        // get user's history
        List<HistoryCell> historyItems = logManager.getLogHistory(uid);
        Item item = itemManager.getItem(itemId);

        if (historyItems.isEmpty() && item == null)
            return null;

        List<Item> historyFilteredItems = new ArrayList<>();
        if (item != null) {
            historyFilteredItems.add(item);
        } else {
            item = historyItems.get(0).getItem();
        }

        // keep history of items which are in the same category with curItem
        for (HistoryCell historyCell : historyItems) {
            if (item.getCategory() == historyCell.getItem().getCategory())
                historyFilteredItems.add(historyCell.getItem());
        }

        historyFilteredItems = historyFilteredItems.subList(0, Math.min(20, historyFilteredItems.size()));
        ItemScoring itemScoring = new ItemScoring(historyFilteredItems);
        List<Location> limitedLocations = itemScoring.getLimitedLocation();

        List<Item> candidates = itemManager.filteringCandidates(limitedLocations, item);

        return scoring(itemScoring, candidates);
    }

    @Override
    public List<Pair<Item, Double>> getRecommend(String uid, int cityId, int cateId, int sellType) {
        List<HistoryCell> historyCells = logManager.getLogHistory(uid);

        List<Integer> cityFilters = new ArrayList<>();
        List<Integer> cateFilters = new ArrayList<>();
        List<Integer> typeFilters = new ArrayList<>();

        // if no filter exists, use filters as value of Top 5 newest viewed items in history
        if (cityId == 0 && cateId == 0 && sellType == 0) {
            for (int i = 0; i < Math.min(5, historyCells.size()); i ++) {
                Item itemInHistory = historyCells.get(i).getItem();
                cityFilters.add(itemInHistory.getLocation().getAddress().getCityId());
                cateFilters.add(itemInHistory.getCategory());
                typeFilters.add(itemInHistory.getSellType());
            }
        }
        else {
            if (cityId > 0)
                cityFilters.add(cityId);
            if (cateId > 0)
                cateFilters.add(cateId);
            if (sellType > 0)
                typeFilters.add(sellType);
        }

        List<Item> history = new ArrayList<>();
        for (HistoryCell h : historyCells)
            history.add(h.getItem());

        ItemScoring itemScoring = new ItemScoring(history);
        List<Item> candidates = itemManager.filteringCandidatesWithoutItemContext(typeFilters, cateFilters, cityFilters);
        System.out.println("No Main Item, candidates.size = " + candidates.size());
        return scoring(itemScoring, candidates);
    }

    /**
     * method uses instance of ItemScoring to calculate score for each candidate item
     *
     * @return Top 50 item with score.
     */
    private List<Pair<Item, Double>> scoring(ItemScoring itemScoring, List<Item> candidates) {
        Map<Item, Double> scoreTotal = new HashMap<>();
        for (Item c : candidates) {
            scoreTotal.put(c, itemScoring.score(c));
        }

        // sorting by score
        List<Map.Entry<Item, Double>> entries = new ArrayList<>(scoreTotal.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(entries);
        double threshold = 0.0;
        List<Pair<Item, Double>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(50, entries.size()) && entries.get(i).getValue() > threshold; i++) {
            double v = Math.round(entries.get(i).getValue() * 100) / 100;
            result.add(new Pair<>(entries.get(i).getKey(), v));
        }

        return result;
    }

    /**
     * Get recommended result with debug, used for preview recommend result
     */
    @Override
    public DebugData getRecommendDebugResult(String uid, Item item) {
        // get user's history
        List<HistoryCell> historyItems = logManager.getLogHistory(uid);

        if (historyItems.isEmpty() && item == null)
            return null;

        List<Item> historyFilteredItems = new ArrayList<>();
        if (item != null) {
            historyFilteredItems.add(item);
        } else {
            item = historyItems.get(0).getItem();
        }

        // keep history of items which are in the same category with curItem
        for (HistoryCell historyCell : historyItems) {
            if (item.getCategory() == historyCell.getItem().getCategory())
                historyFilteredItems.add(historyCell.getItem());
        }

        historyFilteredItems = historyFilteredItems.subList(0, Math.min(20, historyFilteredItems.size()));
        ItemScoring itemScoring = new ItemScoring(historyFilteredItems);
        List<Location> limitedLocations = itemScoring.getLimitedLocation();

        List<Item> candidates = itemManager.filteringCandidates(limitedLocations, item);
        List<Pair<Item, Double>> result = scoring(itemScoring, candidates);

        return new DebugData(result, historyFilteredItems, itemScoring, uid);
    }
}
