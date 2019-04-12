package scoring;

import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.IFeature;
import datastruct.feature.Location;

import java.util.*;

/**
 * Class uses for scoring location feature between new item vs list of history items
 */
public class FLocationScoring extends Scoring<Location> {

    private Map<Location, Double> locWeight;

    public FLocationScoring(List<Item> historyItems) {
        super(historyItems);
    }

    /**
     * extractedFeature stores location's frequency
     */
    protected void featureExtracting() {
        if (this.historyItems == null || this.historyItems.size() == 0)
            return;

        // init resource
        this.locWeight = new HashMap<>();
        this.extractedFeature = new ArrayList<>();
        mainItem = historyItems.get(0);

        // extracting
        Map<Location, Integer> locFrequency = new HashMap<>();
        for (int i = 0; i < historyItems.size(); i ++) {
            Location l = historyItems.get(i).getLocation();
            locFrequency.put(l, 1 + (locFrequency.containsKey(l) ? locFrequency.get(l) : 0));
        }

        for (Location l : locFrequency.keySet()) {
            extractedFeature.add(new Pair<>(l, (double) locFrequency.get(l)));
        }

        weighting();
    }

    public double score(Item item) {
        if (locWeight.containsKey(item.getLocation()))
            return compareVsFilter(item) * locWeight.get(item.getLocation());
        else
            return 0;
    }

    /**
     * calculating & normalizing weight for every location by it's frequency,
     * but set priority for location of current viewing.
     */
    private void weighting() {
        for (Pair<Location, Double> p : extractedFeature) {
            if (p.getLeft().equals(mainItem.getLocation()))
                locWeight.put(p.getLeft(), 30.0);
            else
                locWeight.put(p.getLeft(), 70.0 * p.getRight() / historyItems.size());
        }
        extractedFeature.clear();
        // normalize score to (0; 100]
        double maxScore = Collections.max(locWeight.values());
        for (Location l : new ArrayList<>(locWeight.keySet())) {
            locWeight.put(l, 100 * locWeight.get(l) / maxScore);
        }
        for (Location l : locWeight.keySet())
            extractedFeature.add(new Pair<>(l, locWeight.get(l)));
    }

    /**
     * check if item.location if match with filter.location
     * @param item
     * @return
     */
    private double compareVsFilter(Item item) {
        if (filter == null || this.filter.getFilter(IFeature.FeatureType.LOCATION) == null)
            return 1.0;

        Location ft = (Location) this.filter.getFilter(IFeature.FeatureType.LOCATION);

        // if second level is matched
        if (ft.getAddress().getDistrictId().equals(item.getLocation().getAddress().getDistrictId()))
            return 2.0;
            // if highest level is matched
        else if (ft.getAddress().getCityId().equals(item.getLocation().getAddress().getCityId()))
            return 1.5;
        // if not matched
        return 1.0;
    }
}
