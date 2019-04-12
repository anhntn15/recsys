package scoring;

import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.IFeature;
import datastruct.feature.Price;

import java.util.ArrayList;
import java.util.List;

public class FPriceScoring extends Scoring<Price> {

    /**
     * @preferencePrice: represent for user's preference price.
     */
    private Price preferencePrice;

    public FPriceScoring(List<Item> historyItems) {
        super(historyItems);
    }

    protected void featureExtracting() {
        if (this.historyItems == null || this.historyItems.size() == 0)
            return;

        // init resource
        extractedFeature = new ArrayList<>();

        for (int i = 0; i < historyItems.size(); i ++) {
            Price p = historyItems.get(i).getPrice();
            if (p.getValue() != null)
                extractedFeature.add(new Pair<>(p, 1.0));
        }

        calculatePreferencePrice();
    }

    /**
     * find the most frequent price in a range [average_min; min(max)]
     * @return
     */
    private void calculatePreferencePrice() {
        double min = 0, max = -1;

        for (Pair<Price, Double> p : extractedFeature) {
            min += p.getLeft().value.getLeft();
            if (max < 0)
                max = p.getLeft().value.getRight();
            else
                max = Math.min(max, p.getLeft().value.getRight());
        }

        min /= extractedFeature.size();
        if (max < 0 || max < min)
            max = min * 1.5;

        this.preferencePrice = new Price(new Pair<>(min, max));
    }

    public double score(Item item) {
        // price_score = 100 - x * slope
        int x;
        double slope;

        // check if item does not has price value, then return default score
        if (item.getPrice().getValue() == null)
            return 0.0;

        // get 'representative price' for this item.
        double iPrice = item.getPrice().getValue().getLeft();
        iPrice += item.getPrice().getValue().getRight();
        iPrice /= 2;

        // get 'x' & 'slope'
        if (iPrice < preferencePrice.getValue().getLeft()) {
            x = 1;
            slope = 100.0 * (preferencePrice.getValue().getLeft() - iPrice) / preferencePrice.getValue().getLeft();
        }
        else if (iPrice > preferencePrice.getValue().getRight()) {
            x = 2;
            slope = 100 * (iPrice - preferencePrice.getValue().getRight()) / preferencePrice.getValue().getRight();
        }
        else {
            x = 0;
            slope = 0;
        }

        return Math.max(0, 100.0 - x * slope) + compareVsFilter(item);
    }

    /**
     * calculate the additional score depending on matching ratio between item.price vs filter.price
     */
    private double compareVsFilter(Item item) {
        if (filter == null || this.filter.getFilter(IFeature.FeatureType.PRICE) == null)
            return 1.0;

        Price ft = (Price) this.filter.getFilter(IFeature.FeatureType.PRICE);

        double iMin = item.getPrice().getValue().getLeft();
        double iMax = item.getPrice().getValue().getRight() != null ? item.getPrice().getValue().getRight() : iMin;
        double fMin = ft.value.getLeft();
        double fMax = ft.value.getRight() != null ? ft.value.getRight() : Double.MAX_VALUE;

        // if inside
        if (iMin >= fMin && iMax < fMax)
            return 40.0;
        // if not intersected
        if (iMax < fMax || iMin > fMax)
            return 20.0;
        // if intersected in other cases
        return 0.0;
    }

    @Override
    public String getExtractedFeatureAsHTML() {
        if (extractedFeature == null)
            return "null";

        StringBuilder str = new StringBuilder();
        str.append("<tr>");
        str.append("<th colspan=\"2\">PRICE</th>");
        str.append("</tr>");
        str.append("<tr>");
        str.append("<td>" + this.preferencePrice.value.getLeft() + "</td>");
        str.append("<td>" + this.preferencePrice.value.getRight() + "</td>");
        str.append("</tr>");

        return str.toString();
    }
}
