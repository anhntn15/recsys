package datamanager.helper;

//import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import datastruct.Item;
import datastruct.feature.Location;
import datastruct.feature.Point;

import java.util.concurrent.TimeUnit;

public class DistanceCalculator {
//    private Cache<String, Double> cache;
    private static DistanceCalculator instance = new DistanceCalculator();
    public static final double LOCAL_DISTANCE_KM = 7;

    private DistanceCalculator() {
//        this.cache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.DAYS).build();
    }

    public static DistanceCalculator getInstance() {
        return instance;
    }

    /**
     * distance in kilometer between 2 points
     */
    private double distance(Point p1, Point p2) {
        if (p1 == null || p2 == null)
            return Double.MAX_VALUE;

        double dLat = deg2rad(p1.getLeft() - p2.getLeft());
        double dLon = deg2rad(p1.getRight() - p2.getRight());

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(p1.getLeft())) *
                        Math.cos(deg2rad(p2.getLeft())) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * 6371;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public double getDistance(Item i1, Item i2) {
        if (i1.getId() == i2.getId())
            return 0.0;
//        String key;
//        if (i1.getId() < i2.getId())
//            key = i1.getId() + "_" + i2.getId();
//        else
//            key = i2.getId() + "_" + i1.getId();
//
//        Double distance = cache.getIfPresent(key);
//        if (distance == null) {
//            distance = distance(i1.getLocation().getPoint(), i2.getLocation().getPoint());
//            cache.put(key, distance);
//        }
//
//        return distance;

        return distance(i1.getLocation().getPoint(), i2.getLocation().getPoint());
    }

    public double getDistance(Location loc1, Location loc2) {
        if (loc1.getOnwer() == loc2.getOnwer())
            return 0.0;
//
//        String key;
//        if (loc1.getOnwer() < loc2.getOnwer())
//            key = loc1.getOnwer() + "_" + loc2.getOnwer();
//        else
//            key = loc2.getOnwer() + "_" + loc1.getOnwer();
//
//        Double distance = cache.getIfPresent(key);
//        if (distance == null) {
//            distance = distance(loc1.getPoint(), loc2.getPoint());
//            cache.put(key, distance);
//        }
//
//        return distance;

        return distance(loc1.getPoint(), loc2.getPoint());
    }
}
