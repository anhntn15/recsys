package scoring;

import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.IFeature;
import datastruct.feature.RoomNumber;

import java.util.*;

public class FRoomNumberScoring extends Scoring<RoomNumber> {

    private Map<RoomNumber, Double> roomWeight;

    public FRoomNumberScoring(List<Item> historyItems) {
        super(historyItems);
    }

    protected void featureExtracting() {
        if (this.historyItems == null)
            return;

        // init resource
        extractedFeature = new ArrayList<>();
        roomWeight = new HashMap<>();

        List<Integer> viewedRoomNumbers = new ArrayList<>();
        for (int i = 0; i < historyItems.size(); i ++) {
            if (historyItems.get(i).getRoomNumber().value > 0)
                viewedRoomNumbers.add(historyItems.get(i).getRoomNumber().value);
        }

        double mean = 0.0;
        for (int i : viewedRoomNumbers)
            mean += i;
        if (viewedRoomNumbers.size() > 0) {
            mean /= viewedRoomNumbers.size();

            double scores[] = new double[]{60, 100, 80, 60};
            for (int i = 0; i < scores.length; i++) {
                roomWeight.put(new RoomNumber((int) (i + mean - 1)), scores[i]);
            }
        }

        for (RoomNumber r : roomWeight.keySet()) {
            extractedFeature.add(new Pair<>(r, roomWeight.get(r)));
        }
    }

    public double score(Item item) {
        if (item.getRoomNumber().value != null && roomWeight.containsKey(item.getRoomNumber())) {
            return compareVsFilter(item) * roomWeight.get(item.getRoomNumber());
        }
        else
            return 0;
    }

    private double compareVsFilter(Item item) {
        if (this.filter == null || this.filter.getFilter(IFeature.FeatureType.ROOM_NUMBER) == null)
            return 1.0;
        RoomNumber ft = (RoomNumber) this.filter.getFilter(IFeature.FeatureType.ROOM_NUMBER);

        if (ft.value == item.getRoomNumber().value)
            return 2.0;
        return 1.0;
    }
}
