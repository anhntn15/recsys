package scoring;

import datastruct.Filter;
import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.IFeature;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Class define common behaviors of a scoring features which sharing the same input.
 * Every score which calculated by child-class must be normalized into range of [0;100]
 * @param <T>: FeatureType corresponding to concrete class which implemented.
 */
public abstract class Scoring<T> {
    protected Scoring(List<Item> historyItems) {
        this.historyItems = historyItems;
        featureExtracting();
    }

    /**
     * history items viewed of an user
     */
    protected List<Item> historyItems;

    /**
     * the currently viewing item
     */
    protected Item mainItem;

    /**
     * filter comes from referer of an item, such as listing or query.
     */
    protected Filter filter;

    /**
     * variable holding processed result of featureExtracting()
     * the basic version: just store every features sorted by view_time.
     */
    protected List<Pair<T, Double>> extractedFeature;

    /**
     * extracting common features from input.
     */
    protected abstract void featureExtracting();

    /**
     * calculate similar score for an item.
     * @param item: item need to scoring.
     * @return
     */
    public abstract double score(Item item);

    public String getExtractedFeatureAsHTML() {
        if (extractedFeature == null)
            return "null";

        StringBuilder str = new StringBuilder();
        str.append("<tr>");
        str.append("<th colspan=\"2\">" + ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0] + "</th>");
        str.append("</tr>");
        for (Pair p : extractedFeature) {
            str.append("<tr>");
            str.append("<td>" + p.getLeft() + "</td>");
            str.append("<td>" + p.getRight() + "</td>");
            str.append("</tr>");
        }
        return str.toString();
    }
}
