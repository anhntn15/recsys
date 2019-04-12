package datastruct.feature;

public class RoomNumber extends IFeature<Integer> {
    public RoomNumber(Integer value) {
        super(value);
    }

    public FeatureType getType() {
        return FeatureType.ROOM_NUMBER;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RoomNumber))
            return false;

        RoomNumber other = (RoomNumber) obj;
        return other.value.equals(this.value);
    }

    @Override
    public int hashCode() {
        return this.value.intValue();
    }

    @Override
    public String toString() {
        return value + "";
    }
}
