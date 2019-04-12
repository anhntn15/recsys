package similarity;

import config.RecommenderConst;
import datastruct.Item;

public class ItemSimilarity extends Similarity<Item> {
    private Similarity acr, loc, price, room, content;

    protected ItemSimilarity(Item value) {
        super(value);

        acr = new FAcreageSimilarity(value.getAcreage());
        loc = new FLocationSimilarity(value.getLocation());
        price = new FPriceSimilarity(value.getPrice());
        room = new FRoomNumberSimilarity(value.getRoomNumber());
        content = new FContentSimilarity(value.getContent());
    }

    @Override
    protected double getSimilar(Item other) {
        double similarity = RecommenderConst.W_ACREAGE * acr.getSimilar(other.getAcreage()) +
                            RecommenderConst.W_CONTENT * content.getSimilar(other.getContent()) +
                            RecommenderConst.W_LOCATION * loc.getSimilar(other.getLocation()) +
                            RecommenderConst.W_PRICE * price.getSimilar(other.getPrice()) +
                            RecommenderConst.W_ROOM_NUMBER * room.getSimilar(other.getRoomNumber());

        return Math.max(similarity, 0);
    }
}
