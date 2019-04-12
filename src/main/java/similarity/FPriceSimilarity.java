package similarity;

import datastruct.feature.Price;

public class FPriceSimilarity extends Similarity<Price> {
    protected FPriceSimilarity(Price value) {
        super(value);
    }

    @Override
    protected double getSimilar(Price other) {
        // if the price is not specified
        if (value.value == null || other.value == null)
            return 0.0;

        // in case of intersection
        if (value.getMin() >= other.getMax() || value.getMax() <= other.getMin())
            return 100.0;

        // calculate how much difference between value's price & other's price
        double diff;
        if (value.getMin() > other.getMax())
            diff = 100.0 * (value.getMin() - other.getMax()) / value.getMin();
        else
            diff = 100.0 * (other.getMin() - value.getMax()) / value.getMax();

        return 100 - diff - 5 * Math.floor(diff / 20.0);
    }
}
