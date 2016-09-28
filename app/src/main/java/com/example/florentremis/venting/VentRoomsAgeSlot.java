package com.example.florentremis.venting;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class VentRoomsAgeSlot {

    private String title;
    private String roomId;
    private long creationTime;
    private String venterId;


    public VentRoomsAgeSlot() {
    }

    public VentRoomsAgeSlot(String title, String roomId, long creationTime, String venterId) {
        this.title = title;
        this.roomId = roomId;
        this.creationTime = creationTime;
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
        result.put("venterId", venterId);

        return result;
    }
}