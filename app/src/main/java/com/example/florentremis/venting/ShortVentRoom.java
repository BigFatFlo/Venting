package com.example.florentremis.venting;

public class ShortVentRoom {

    private String title;
    private String roomId;
    private long creationTime;
    private long lastUpdateTime;
    private long timeLeft;
    private Boolean locked;

    public ShortVentRoom() {
    }

    public ShortVentRoom(String title, String roomId, long creationTime, long lastUpdateTime, long timeLeft, Boolean locked) {
        this.title = title;
        this.roomId = roomId;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.timeLeft = timeLeft;
        this.locked = locked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
