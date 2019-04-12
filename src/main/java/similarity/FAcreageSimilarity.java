package similarity;

import datastruct.feature.Acreage;

public class FAcreageSimilarity extends Similarity<Acreage> {
    protected FAcreageSimilarity(Acreage value) {
        super(value);
    }

    @Override
    protected double getSimilar(Acreage other) {
        // if the acreage is not specified
        if (value.value == null || other.value == null)
            return 0.0;

        // in case of intersection
        if (value.getMin() >= other.getMax() || value.getMax() <= other.getMin())
            return 100.0;

        // calculate how much difference between value's acreage & other's acreage
        double diff;
        if (value.getMin() > other.getMax())
            diff = 100.0 * (value.getMin() - other.getMax()) / value.getMin();
        else
            diff = 100.0 * (other.getMin() - value.getMax()) / value.getMax();

        return 100 - 0.5 * diff;
    }
}
