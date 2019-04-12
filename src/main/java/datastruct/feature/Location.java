package datastruct.feature;

import datastruct.Pair;
import utils.LocationName;

public class Location extends IFeature<Pair<Address, Point>> {
    public Location(Pair<Address, Point> value) {
        super(value);
    }

    public Location(Address a, Point p) {
        super(new Pair<>(a, p));
    }

    public FeatureType getType() {
        return FeatureType.LOCATION;
    }

    public Address getAddress() {
        return value.getLeft();
    }

    public Point getPoint() {
        return value.getRight();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location))
            return false;

        Location other = (Location) obj;
        return this.value.getLeft().getRight().equals(other.value.getLeft().getRight());
    }

    @Override
    public int hashCode() {
        return (value.getLeft().getRight()).hashCode();
    }

    @Override
    public String toString() {
        return LocationName.getCity(value.getLeft().getLeft().intValue()) + ":" + LocationName.getDistrict(value.getLeft().getRight().intValue());
    }
}
