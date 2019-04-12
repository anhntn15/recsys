package similarity;

import datamanager.helper.ContentSimilarManager;
import datastruct.feature.Content;

public class FContentSimilarity extends Similarity<Content> {
    private ContentSimilarManager similarManager;

    protected FContentSimilarity(Content value) {
        super(value);
        similarManager = ContentSimilarManager.getInstance();
    }

    @Override
    protected double getSimilar(Content other) {
        return similarManager.getSimilar(value.getOnwer(), other.getOnwer());
    }
}
