package scoring;

import config.RecommenderConst;
import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.Location;

import java.util.*;

/**
 * Scoring for an item.
 */
public class ItemScoring extends Scoring {
    private Scoring acr, content, loc, price, room;

    public ItemScoring(List<Item> historyItems) {
        super(historyItems);

        acr = new FAcreageScoring(historyItems);
        content = new FContentScoring(historyItems);
        loc = new FLocationScoring(historyItems);
        price = new FPriceScoring(historyItems);
        room = new FRoomNumberScoring(historyItems);
    }

    protected void featureExtracting() {
        // do nothing
    }

    public List<Location> getLimitedLocation() {
        List<Location> locs = new ArrayList<>();
        for (Object p : loc.extractedFeature)
            locs.add(((Pair<Location, Double>) p).getLeft());
        return locs;
    }

    public double score(Item item) {
        if (historyItems.size() == 0) {
            return 1.0;
        }
        double score = RecommenderConst.W_ACREAGE * acr.score(item) +
                        RecommenderConst.W_CONTENT * content.score(item) +
                        RecommenderConst.W_LOCATION * loc.score(item) +
                        RecommenderConst.W_PRICE * price.score(item) +
                        RecommenderConst.W_ROOM_NUMBER * room.score(item);

        return Math.max(0, score);
    }

    @Override
    public String getExtractedFeatureAsHTML() {
        StringBuilder str = new StringBuilder();
        str.append("<table border=\"1\">");
        str.append("<tr>");
        str.append(loc.getExtractedFeatureAsHTML());
        str.append(acr.getExtractedFeatureAsHTML());
        str.append(price.getExtractedFeatureAsHTML());
        str.append(room.getExtractedFeatureAsHTML());
        str.append("</tr>");
        str.append("</table>");
        return str.toString();
    }
}
