package similarity;

/**
 * Class define common behaviors of a when computing similarity between item-item or feature-feature
 * Every score which calculated by child-class must be normalized into range of [0;100]
 * @param <T>: FeatureType corresponding to concrete class which implemented.
 */
public abstract class Similarity<T> {
    /**
     * object is used as model (eg: item in history) need to be compared with other candidate
     */
    protected T value;

    protected Similarity(T value) {
        this.value = value;
    }

    /**
     * get similarity of feature T between main object & other object
     * high score is BETTER
     */
    protected abstract double getSimilar(T other);
}
