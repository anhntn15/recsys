package similarity;

import datamanager.helper.DistanceCalculator;
import datastruct.feature.Location;

public class FLocationSimilarity extends Similarity<Location> {
    protected FLocationSimilarity(Location value) {
        super(value);
    }

    @Override
    protected double getSimilar(Location other) {
        if (value.getAddress().getDistrictId() == other.getAddress().getDistrictId())
            return 100;

        if (value.getAddress().getCityId() != other.getAddress().getCityId())
            return 0;

        return scoreByDistance(DistanceCalculator.getInstance().getDistance(value, other));
    }

    /**
     * return 100 if 2 points is local area
     * fine 20 point for every 10 KM farther
     */
    private double scoreByDistance(double distance) {
        if (distance <= DistanceCalculator.LOCAL_DISTANCE_KM)
            return 100;

        return 100 - 20 *(1 + (distance / 10));
    }

}
