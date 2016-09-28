package com.example.florentremis.venting;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class VentRoomStringTimes {

    private String title;
    private String roomId;
    private Map<String, String> creationTime;
    private Map<String, String> lastUpdateTime;
    private long timeLeft;
    private Boolean locked;
    private String venterId;

    public VentRoomStringTimes() {
    }

    public VentRoomStringTimes(String title, String roomId, Map<String, String> creationTime, Map<String, String> lastUpdateTime, long timeLeft, Boolean locked, String venterId) {
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

    public Map<String, String> getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Map<String, String> creationTime) {
        this.creationTime = creationTime;
    }

    public Map<String, String> getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Map<String, String> lastUpdateTime) {
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