package datastruct.feature;

import datastruct.Pair;

public class Point extends Pair<Double, Double> {
    public Point(Double left, Double right) {
        // latitude - longitude
        super(left, right);
    }

    @Override
    public String toString() {
        return getLeft() + " - " + getRight();
    }
}
