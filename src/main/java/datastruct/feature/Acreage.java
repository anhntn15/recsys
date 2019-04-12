package datastruct.feature;

import datastruct.Pair;

public class Acreage extends IFeature<Pair<Double, Double>> {

    public Acreage(Pair<Double, Double> value) {
        super(validate(value.getLeft(), value.getRight()));
    }

    public Acreage(double min, double max) {
        super(validate(min, max));
    }

    private static Pair<Double, Double> validate(double min, double max) {
        if (min == 0.0 && max == 0.0)
            return null;

        if (min == 0.0 || max == 0.0)
            min = max = Math.max(min, max);

        return new Pair<>(min, max);
    }

    public Double getMin() {
        return this.value.getLeft();
    }

    public Double getMax() {
        return this.value.getRight();
    }

    public FeatureType getType() {
        return FeatureType.ACREAGE;
    }

    @Override
    public String toString() {
        return value != null ? String.format("%.2f->%.2f", value.getLeft(), value.getRight()) : "";
    }
}
