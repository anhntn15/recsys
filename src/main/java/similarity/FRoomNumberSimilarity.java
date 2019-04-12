package similarity;

import datastruct.feature.RoomNumber;

public class FRoomNumberSimilarity extends Similarity<RoomNumber> {
    private final double SCORES[] = new double[]{60, 100, 80, 60};

    protected FRoomNumberSimilarity(RoomNumber value) {
        super(value);
    }

    @Override
    protected double getSimilar(RoomNumber other) {
        if (value.getValue() == 0 || other.getValue() == 0)
            return 0.0;

        int diff = other.value - value.value;

        if (diff >= -1 && diff <= 2)
            return SCORES[diff + 1];

        return 0.0;
    }
}
