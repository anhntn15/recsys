package datastruct.feature;

public class Content extends IFeature<String> {
    public Content(String value) {
        super(value != null ? value : "");
    }

    public FeatureType getType() {
        return FeatureType.CONTENT;
    }
}
