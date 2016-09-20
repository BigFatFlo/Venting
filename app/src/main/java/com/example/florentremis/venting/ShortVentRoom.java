package com.example.florentremis.venting;

public class ShortVentRoom {

    private String title;
    private String roomId;

    public ShortVentRoom() {
    }

    public ShortVentRoom(String title, String roomId) {
        this.title = title;
        this.roomId = roomId;
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
}
