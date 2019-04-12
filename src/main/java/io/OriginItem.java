package io;

import datastruct.Item;
import datastruct.Pair;
import datastruct.feature.*;

import java.util.Date;

public class OriginItem {
    Long id;
    String title;
    String content;
    Integer sellType;
    Double minPrice;
    Double maxPrice;
    Double minArea;
    Double maxArea;
    Integer cateId;
    Integer cityId;
    Integer districtId;
    Integer numBedRoom;
    Integer viewCount;
    Integer callCount;
    Date startDate;
    Date endDate;

    public OriginItem(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public OriginItem(Long id, String title, String content, Integer sellType, Double minPrice, Double maxPrice,
                      Double minArea, Double maxArea, Integer cateId, Integer cityId, Integer districtId,
                      Integer numBedRoom, Integer viewCount, Integer callCount, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sellType = sellType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.cateId = cateId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.numBedRoom = numBedRoom;
        this.viewCount = viewCount;
        this.callCount = callCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("@%s[id=%s, title=%s, content=%s, sellType=%s, minPrice=%s, maxPrice=%s, minArea=%s," +
                        " maxArea=%s, cateId=%s, cityId=%s, districtId=%s, numBedRoom=%s, viewCount=%s, callCount=%s, startDate=%s, endDate=%s]",
                getClass(), id, title, content, sellType, minArea, maxArea, minArea, maxArea, cateId, cityId, districtId, numBedRoom, viewCount, callCount, startDate, endDate);
    }

    public Item toItem() {
        Item item = new Item();
        item.setId(id);
        item.setAcreage(new Acreage(new Pair<>(minArea, maxArea)));
        item.setPrice(new Price(new Pair<>(minPrice, maxPrice)));
        item.setCategory(cateId);
        item.setContent(new Content(content));
        if (numBedRoom != null)
            item.setRoomNumber(new RoomNumber(numBedRoom));
        else item.setRoomNumber(new RoomNumber(0));
        item.setLocation(new Location(new Pair<>(new Address(cityId, districtId), null)));

        return item;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getSellType() {
        return sellType;
    }

    public Integer getCateId() {
        return cateId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public Integer getNumBedRoom() {
        return numBedRoom;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getCallCount() {
        return callCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public Double getMinArea() {
        return minArea;
    }

    public Double getMaxArea() {
        return maxArea;
    }
}
