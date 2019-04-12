package recommender;

import datamanager.ItemManager;
import datamanager.UserLogManager;
import datastruct.HistoryCell;
import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.Location;
import similarity.HistorySimilarity;

import java.util.*;

/**
 * Content based recommender - version 2
 */
public class ContentBasedRecommender extends RecommendAbstract {
    private static ContentBasedRecommender instance = null;

    private ContentBasedRecommender(ItemManager itemManager, UserLogManager logManager) {
        super(itemManager, logManager);
    }

    public static void init(ItemManager itemManager, UserLogManager logManager) {
        if (instance == null) {
            instance = new ContentBasedRecommender(itemManager, logManager);
        }
    }

    public static ContentBasedRecommender getInstance() {
        return instance;
    }

    @Override
    public List<Pair<Item, Double>> getRecommend(String uid, long itemId) {
        List<HistoryCell> historyItems = logManager.getLogHistory(uid);
        Item curItem = itemManager.getItem(itemId);

        if (historyItems.isEmpty() && curItem == null)
            return null;

        List<HistoryCell> historyFilteredItems = new ArrayList<>();
        if (curItem != null) {
            historyFilteredItems.add(new HistoryCell(curItem));
        } else {
            if (historyItems.isEmpty()) {
                return null;
            }
            curItem = historyItems.get(0).getItem();
        }

        // keep history of items which are in the same category with curItem
        for (HistoryCell historyCell : historyItems) {
            if (curItem.getCategory() == historyCell.getItem().getCategory())
                historyFilteredItems.add(historyCell);
        }

        HistorySimilarity historySimilarity = new HistorySimilarity(historyFilteredItems);
        List<Location> limitedLocations = historySimilarity.getLimitedLocations();
        List<Item> candidates = itemManager.filteringCandidates(limitedLocations, curItem);

        return scoring(historySimilarity, candidates);
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

        HistorySimilarity calculator = new HistorySimilarity(historyCells);
        List<Item> candidates = itemManager.filteringCandidatesWithoutItemContext(typeFilters, cateFilters, cityFilters);
        System.out.println("No Main Item, candidates.size = " + candidates.size());
        return scoring(calculator, candidates);
    }

    private List<Pair<Item, Double>> scoring(HistorySimilarity calculator, List<Item> candidates) {
        Map<Item, Double> scoreTotal = new HashMap<>();
        for (Item c : candidates) {
            scoreTotal.put(c, calculator.getSimilar(c));
        }
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

    @Override
    public DebugData getRecommendDebugResult(String uid, Item curItem) {
        List<HistoryCell> historyItems = logManager.getLogHistory(uid);

        if (historyItems.isEmpty() && curItem == null)
            return null;

        List<HistoryCell> historyFilteredItems = new ArrayList<>();
        if (curItem != null) {
            historyFilteredItems.add(new HistoryCell(curItem));
        } else {
            curItem = historyItems.get(0).getItem();
        }

        // keep history of items which are in the same category with curItem
        List<Item> history4Preview = new ArrayList<>();
        for (HistoryCell historyCell : historyItems) {
            if (curItem.getCategory() == historyCell.getItem().getCategory()) {
                historyFilteredItems.add(historyCell);
                history4Preview.add(historyCell.getItem());
            }
        }

        HistorySimilarity historySimilarity = new HistorySimilarity(historyFilteredItems);
        List<Location> limitedLocations = historySimilarity.getLimitedLocations();
        List<Item> candidates = itemManager.filteringCandidates(limitedLocations, curItem);

        List<Pair<Item, Double>> result = scoring(historySimilarity, candidates);

        return new DebugData(result, history4Preview, null, uid);
    }
}
