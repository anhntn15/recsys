package io;

import java.util.Date;

public class LogObject {
    private String id;
    private String uid;
//    private Item item;
    private Long itemId;
    private Date createTime = null;
    private Date dbUpdateTime = null;
    private int timeOnSite=0;
    private int clickToCall = 0;

    public LogObject(String id, String uid, Long item) {
        this.id = id;
        this.uid = uid;
        this.itemId = item;
    }

    public LogObject(String uid, Long item) {
        this.uid = uid;
        this.itemId = item;
    }

    public LogObject(String id, String uid, Long item, Date createTime) {
        this.id = id;
        this.uid = uid;
        this.itemId = item;
        this.createTime = createTime;
    }

    public LogObject(String id, String uid, Long item, Date createTime, Date dbUpdateTime) {
        this.id = id;
        this.uid = uid;
        this.itemId = item;
        this.createTime = createTime;
        this.dbUpdateTime = dbUpdateTime;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public Long getItemId() {
        return itemId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getTimeOnSite() {
        return timeOnSite;
    }

    public Date getDbUpdateTime() {
        return dbUpdateTime;
    }

    public boolean isClickToCall() {
        return clickToCall == 1;
    }

    public int getClickToCall() {
        return clickToCall;
    }

    public void setTimeOnSite(int timeOnSite) {
        this.timeOnSite = timeOnSite;
    }

    public void setClickToCall(int clickToCall) {
        this.clickToCall = clickToCall;
    }
}
