package scoring;

import datastruct.Item;
import datastruct.feature.Content;
import datamanager.helper.ContentSimilarManager;

import java.util.List;

public class FContentScoring extends Scoring<Content> {
    public FContentScoring(List<Item> historyItems) {
        super(historyItems);
    }

    protected void featureExtracting() {

    }

    /**
     * Cong score voi cac bai history
     *
     * @param item: item need to scoring.
     * @return
     */
    public double score(Item item) {
        double score = 0;
        for (Item itemHistory : historyItems) {
            score += datamanager.helper.ContentSimilarManager.getInstance().getSimilar(itemHistory, item);
        }
        return score;
    }

    /**
     * get score khi cho 1 item id
     * Cong score voi cac bai history
     * @param itemId
     * @return
     */
    public double score(Long itemId){
        double score = 0;
        for (Item item : historyItems) {
            score += ContentSimilarManager.getInstance().getSimilar(item.getId(), itemId);
        }
        return score;
    }
}
