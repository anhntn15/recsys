package scoring;

import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.Acreage;
import datastruct.feature.IFeature;

import java.util.*;

public class FAcreageScoring extends Scoring<Acreage> {

    /**
     * @preferenceSquare: represent for user's preference acreage.
     */
    private Acreage preferenceAcreage;

    /**
     * @filterAcreage:
     */
    private Acreage filterAcreage;

    public FAcreageScoring(List<Item> historyItems) {
        super(historyItems);
        if (filter == null || this.filter.getFilter(IFeature.FeatureType.ACREAGE) == null)
            filterAcreage = null;
        else {
            filterAcreage = (Acreage) this.filter.getFilter(IFeature.FeatureType.ACREAGE);
        }
    }

    protected void featureExtracting() {
        if (this.historyItems == null || this.historyItems.size() == 0)
            return;

        // init resource
        this.extractedFeature = new ArrayList<>();
        mainItem = historyItems.get(0);

        // process
        for (int i = 0; i < historyItems.size(); i ++) {
            Acreage a = historyItems.get(i).getAcreage();
            if (a.getValue() != null)
                extractedFeature.add(new Pair<>(a, 1.0));
        }

        calculatePreferenceAcreage();
    }


    /**
     * find the most frequent acreage in a range [average_min; min(max)]
     * @return
     */
    private void calculatePreferenceAcreage() {
        double min = 0, max = -1;

        for (Pair<Acreage, Double> p : extractedFeature) {
            min += p.getLeft().value.getLeft();
            if (max < 0)
                max = p.getLeft().value.getRight();
            else
                max = Math.min(max, p.getLeft().value.getRight());
        }

        min /= extractedFeature.size();
        if (max < 0 || max < min)
            max = min * 1.3;

        this.preferenceAcreage = new Acreage(new Pair<>(min, max));
    }

    public double score(Item item) {
        // acreage_score = 100 - x * slope
        int x;
        double slope, score;

        // check if item does not has acreage value, then return default score
        if (item.getAcreage().getValue() == null)
            return 0.0;

        // get 'representative acreage' for this item.
        double iAcreage = item.getAcreage().getValue().getLeft();
        iAcreage += item.getAcreage().getValue().getRight();
        iAcreage /= 2;

        // get 'x' & 'slope'
        if (iAcreage < preferenceAcreage.getValue().getLeft()) {
            x = 1;
            slope = 100.0 * (preferenceAcreage.getValue().getLeft() - iAcreage) / preferenceAcreage.getValue().getLeft();
        }
        else if (iAcreage > preferenceAcreage.getValue().getRight()) {
            x = 2;
            slope = 100 * (iAcreage - preferenceAcreage.getValue().getRight()) / preferenceAcreage.getValue().getRight();
        }
        else {
            x = 0;
            slope = 0;
        }

        score = Math.max(0, 100.0 - x * slope);
        double ratio = compareVsFilter(item);

        return score * ratio;
    }

    /**
     * calculate the rate of intersection between filter range and item.price.value
     * @param item to calculate ratio
     * @return intersected ratio
     */
    private double compareVsFilter(Item item) {
        if(filterAcreage == null){
            return 1.0;
        }
        double iMin = item.getAcreage().getValue().getLeft();
        double iMax = item.getAcreage().getValue().getRight() != null ? item.getAcreage().getValue().getRight() : iMin;
        double fMin = filterAcreage.value.getLeft();
        double fMax = filterAcreage.value.getRight() != null ? filterAcreage.value.getRight() : Double.MAX_VALUE;

        // if inside
        if (iMin >= fMin && iMax < fMax)
            return 2.0;
        // if not intersected
        if (iMax < fMax || iMin > fMax)
            return 1.0;
        // if intersected in other cases
        return 1.5;
    }

    @Override
    public String getExtractedFeatureAsHTML() {
        if (extractedFeature == null)
            return "null";

        StringBuilder str = new StringBuilder();
        str.append("<tr>");
        str.append("<th colspan=\"2\">ACREAGE</th>");
        str.append("</tr>");
        str.append("<tr>");
        str.append("<td>" + this.preferenceAcreage.value.getLeft() + "</td>");
        str.append("<td>" + this.preferenceAcreage.value.getRight() + "</td>");
        str.append("</tr>");

        return str.toString();
    }
}
