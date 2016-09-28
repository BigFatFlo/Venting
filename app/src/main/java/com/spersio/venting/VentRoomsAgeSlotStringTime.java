package com.spersio.venting;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class VentRoomsAgeSlotStringTime {

    private String title;
    private String roomId;
    private Map<String, String> creationTime;
    private String venterId;


    public VentRoomsAgeSlotStringTime() {
    }

    public VentRoomsAgeSlotStringTime(String title, String roomId, Map<String, String> creationTime, String venterId) {
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

    public Map<String, String> getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Map<String, String> creationTime) {
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