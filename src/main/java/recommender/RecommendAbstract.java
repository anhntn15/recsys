package recommender;

import datamanager.ItemManager;
import datamanager.UserLogManager;
import datastruct.Item;
import datastruct.Pair;

import java.util.List;

/**
 * Recommend abstract class
 */
public abstract class RecommendAbstract {
    protected ItemManager itemManager;
    protected UserLogManager logManager;

    RecommendAbstract(ItemManager itemManager, UserLogManager logManager) {
        this.itemManager = itemManager;
        this.logManager = logManager;
    }

    /**
     * Abstract method be used in context of viewing specific item
     *
     * @param uid: must specified
     * @param itemId: must specified in runtime (may be null in debugging api)
     *
     * @return list of recommended items, can be an empty list.
     */
    public abstract List<Pair<Item, Double>> getRecommend(String uid, long itemId);

    /**
     * Abstract method for recommendation with no item viewing context
     * Used in homepage, listing page, blog page, ...
     *
     * @param uid: must specified
     * @param cityId: can be null
     * @param cateId: can be null
     * @param sellType: can be null
     *
     * @return list of recommended items, can be an empty list.
     */
    public abstract List<Pair<Item, Double>> getRecommend(String uid, int cityId, int cateId, int sellType);

    /**
     * return some other features of recommender for debugging/review output
     */
    public abstract DebugData getRecommendDebugResult(String uid, Item item);
}
