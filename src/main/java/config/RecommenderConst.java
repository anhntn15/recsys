package config;

public class RecommenderConst {
    private static final double[] NORMALIZED_VALUES = normalize();

    public static final double W_LOCATION = NORMALIZED_VALUES[0];
    public static final double W_PRICE = NORMALIZED_VALUES[1];
    public static final double W_ACREAGE = NORMALIZED_VALUES[2];
    public static final double W_CONTENT = NORMALIZED_VALUES[3];
    public static final double W_ROOM_NUMBER = NORMALIZED_VALUES[4];

    private static double[] normalize() {
        Resources resources = Resources.getInstance();
        double w_loc = Double.valueOf(resources.getProperty("weight_location"));
        double w_price = Double.valueOf(resources.getProperty("weight_price"));
        double w_acr = Double.valueOf(resources.getProperty("weight_acreage"));
        double w_content = Double.valueOf(resources.getProperty("weight_content"));
        double w_room = Double.valueOf(resources.getProperty("weight_room_number"));

        double s = w_loc + w_price + w_acr + w_content + w_room;
        w_loc /= s;
        w_price /= s;
        w_acr /= s;
        w_content /= s;
        w_room /= s;

        return new double[] {w_loc, w_price, w_acr, w_content, w_room};
    }
}
