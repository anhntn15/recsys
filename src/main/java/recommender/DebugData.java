package recommender;

import datastruct.Item;
import datastruct.Pair;
import scoring.ItemScoring;

import java.util.List;

/**
 * Keep data for preview recommend result
 */
public class DebugData {
    private List<Pair<Item, Double>> result;
    private List<Item> historyItems;
    private ItemScoring itemScoring;
    private String randomUId;

    public DebugData(List<Pair<Item, Double>> result, List<Item> historyItems, ItemScoring itemScoring, String randomUId) {
        this.result = result;
        this.historyItems = historyItems;
        this.itemScoring = itemScoring;
        this.randomUId = randomUId;
    }

    public List<Pair<Item, Double>> getResult() {
        return result;
    }

    public List<Item> getHistoryItems() {
        return historyItems;
    }

    public ItemScoring getItemScoring() {
        return itemScoring;
    }

    public String getRandomUId() {
        return randomUId;
    }
}
