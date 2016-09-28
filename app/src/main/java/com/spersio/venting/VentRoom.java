package com.spersio.venting;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class VentRoom {

    private String title;
    private String roomId;
    private long creationTime;
    private long lastUpdateTime;
    private long timeLeft;
    private Boolean locked;
    private String venterId;

    public VentRoom() {
    }

    public VentRoom(String title, String roomId, long creationTime, long lastUpdateTime, long timeLeft, Boolean locked, String venterId) {
        this.title = title;
        this.roomId = roomId;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.timeLeft = timeLeft;
        this.locked = locked;
        this.venterId = venterId;
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

    public String getVenterId() {
        return venterId;
    }

    public void setVenterId(String venterId) {
        this.venterId = roomId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("roomId", roomId);
        result.put("creationTime", creationTime);
        result.put("lastUpdateTime", lastUpdateTime);
        result.put("timeLeft", timeLeft);
        result.put("locked", locked);
        result.put("venterId", venterId);

        return result;
    }
}