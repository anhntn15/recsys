package datastruct;

import java.util.Date;

public class HistoryCell {
    private Item item;
    private int timeOnSite;
    private boolean clickToCall;
    private Date createTime =null;

    public HistoryCell(Item item) {
        this.item = item;
        timeOnSite = 0;
        clickToCall = false;
    }

    public HistoryCell(Item items, int timeOnSite, boolean clickToCall) {
        this.item = items;
        this.timeOnSite = timeOnSite;
        this.clickToCall = clickToCall;
    }

    public HistoryCell(Item items, int timeOnSite, boolean clickToCall, Date insertTime) {
        this.item = items;
        this.timeOnSite = timeOnSite;
        this.clickToCall = clickToCall;
        this.createTime = insertTime;
    }

    public Item getItem() {
        return item;
    }

    public int getTimeOnSite() {
        return timeOnSite;
    }

    public boolean isClickToCall() {
        return clickToCall;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
