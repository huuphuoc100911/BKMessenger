package com.example.bkmessenger;

public class ZaloDisplay {
    private String username;
    private String message;
    private String id_unique;

    public ZaloDisplay(String username, String message, String id_unique) {
        this.username = username;
        this.message = message;
        this.id_unique = id_unique;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_unique() {
        return id_unique;
    }

    public void setId_unique(String id_unique) {
        this.id_unique = id_unique;
    }
}
