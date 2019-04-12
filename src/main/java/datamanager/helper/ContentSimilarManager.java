package datamanager.helper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import datamanager.ItemManager;
import datastruct.Item;
import nlp.CosineSimilarWithNormal;
import nlp.ContentSimilarity;

import java.util.concurrent.TimeUnit;

/**
 * Quản lý việc tính độ tương đồng của các item
 * Class chỉ tạo được 1 instance duy nhất, được tạo bởi method init.
 */
public class ContentSimilarManager {
    private ItemManager itemManager;
    private ContentSimilarity similarity;
    private Cache<String, Double> cache;

    private static ContentSimilarManager instance = null;

    private ContentSimilarManager(ItemManager itemManager) {
        this.itemManager = itemManager;
        this.similarity = new CosineSimilarWithNormal();
        this.cache =  CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.DAYS).build();
    }

    /**
     * Tạo instance nếu chưa được tạo
     * @param itemManager
     */
    public static void init(ItemManager itemManager) {
        if (instance == null) {
            instance = new ContentSimilarManager(itemManager);
        }
    }

    /**
     * @return
     */
    public static ContentSimilarManager getInstance() {
        return instance;
    }

    /**
     * get cosine similar
     *
     * @param itemId1 item id
     * @param itemId2 item id
     * @return similar score
     */
    public double getSimilar(Long itemId1, Long itemId2) {
        return getSimilar(itemManager.getItem(itemId1), itemManager.getItem(itemId2));
//        if (itemId1.compareTo(itemId2) > 0) {
//            Long temp = itemId1;
//            itemId1 = itemId2;
//            itemId2 = temp;
//        }
//        String key = itemId1 + "_" + itemId2;
//        Double score = cache.getIfPresent(key);
//        if (score == null) {
//            Item item1 = itemManager.getItem(itemId1);
//            Item item2 = itemManager.getItem(itemId2);
//            if (item1 == null || item2 == null) {
//                score = 0.0;
//            } else {
//                score = this.similarity.getSimilar(item1.getTfidf(), item2.getTfidf());
//                cache.put(key, score);
//            }
//        }
//        return score;
    }

    /**
     * get cosine similar
     *
     * @param item1 item object
     * @param item2 item object
     * @return similar score
     */
    public double getSimilar(Item item1, Item item2) {
        return this.similarity.getSimilar(item1.getTfidf(), item2.getTfidf());
//        Long itemId1 = item1.getId();
//        Long itemId2 = item2.getId();
//        if (itemId1.compareTo(itemId2) > 0) {
//            Long temp = itemId1;
//            itemId1 = itemId2;
//            itemId2 = temp;
//        }
//        String key = itemId1 + "_" + itemId2;
//        Double score = cache.getIfPresent(key);
//        if (score == null) {
//            score = this.similarity.getSimilar(item1.getTfidf(), item2.getTfidf());
//            cache.put(key, score);
//        }
//        return score;
    }
}
