package datastruct.feature;

import datastruct.Pair;

public class Address extends Pair<Integer, Integer> {
    public Address(Integer city, Integer district) {
        super(city, district);
    }

    public Integer getCityId() {
        return this.getLeft();
    }

    public Integer getDistrictId() {
        return this.getRight();
    }

}
