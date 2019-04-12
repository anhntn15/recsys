package datastruct;

import datastruct.feature.*;

import java.lang.reflect.Field;
import java.util.Map;

public class Item {
    private Long id;
    private int sellType;
    private Acreage acreage;
    private Location location;
    private Price price;
    private Content content;
    private RoomNumber roomNumber;
    private int category;
    private Map<String, Double> tfidf;

    public Item(Long id) {
        this.id = id;
    }

    public Item(Long id, Acreage acreage, Location location, Price price, Content content, RoomNumber roomNumber, Integer sellType, int category) {
        this.id = id;
        this.sellType = sellType;
        this.acreage = acreage;
        this.location = location;
        this.price = price;
        this.content = content;
        this.roomNumber = roomNumber;
        this.category = category;

        setOwner();
    }

    public void setOwner() {
        this.location.setOnwer(this.id);
        this.content.setOnwer(this.id);
    }

    public Item() {}

    public void setTfidf(Map<String, Double> tfidf) {
        this.tfidf = tfidf;
    }

    public Map<String, Double> getTfidf() {
        return tfidf;
    }

    public Long getId() {
        return id;
    }

    public int getSellType() {
        return sellType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Acreage getAcreage() {
        return acreage;
    }

    public void setAcreage(Acreage acreage) {
        this.acreage = acreage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public RoomNumber getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(RoomNumber roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
