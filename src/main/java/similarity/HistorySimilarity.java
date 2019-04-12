package similarity;

import datastruct.HistoryCell;
import datastruct.Item;
import datastruct.feature.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class calculates how similar between a candidate item vs history (list of item)
 */
public class HistorySimilarity extends Similarity<Item> {
    private Map<ItemSimilarity, Double> simCalculator;
    private double totalWeight;
    private List<Location> limitedLocations;

    public HistorySimilarity(List<HistoryCell> history) {
        super(null);
        this.simCalculator = new HashMap<>();
        this.limitedLocations = new ArrayList<>();
        preprocess(history);
    }

    /**
     * pre-process user's history:
     * - assigning weight for each item in history
     * - extract location appeared in history
     */
    private void preprocess(List<HistoryCell> history) {
        totalWeight = 0;
        for (HistoryCell h : history) {
            double weightOfItem = 1;

            if (h.isClickToCall())
                weightOfItem += 1;

            // increase 1 point for weight for each 2 reading minutes longer
            weightOfItem += 1 * (Math.ceil(h.getTimeOnSite() / 120.0));
            simCalculator.put(new ItemSimilarity(h.getItem()), weightOfItem);

            totalWeight += weightOfItem;

            // location filtering
            if (!limitedLocations.contains(h.getItem().getLocation()))
                limitedLocations.add(h.getItem().getLocation());
        }
    }

    public List<Location> getLimitedLocations() {
        return limitedLocations;
    }

    @Override
    public double getSimilar(Item other) {
        if (simCalculator.size() == 0)
            return 0;

        double totalSimilar = 0;
        for (Map.Entry<ItemSimilarity, Double> model : simCalculator.entrySet()) {
            totalSimilar += model.getKey().getSimilar(other) * model.getValue();
        }
        return Math.ceil(totalSimilar / totalWeight);
    }
}
