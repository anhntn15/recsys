package datastruct.feature;

/**
 * Class represents common attribute & behavior of a item.feature
 * @param <V>: DataType of corresponding feature.
 */
public abstract class IFeature<V> {
    public enum FeatureType {
        ACREAGE,
        PRICE,
        LOCATION,
        CONTENT,
        ROOM_NUMBER
    }

    public IFeature(V value) {
        this.value = value;
    }

    public V value;

    protected Long onwer;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Long getOnwer() {
        return onwer;
    }

    public void setOnwer(Long onwer) {
        this.onwer = onwer;
    }

    public abstract FeatureType getType();
}
