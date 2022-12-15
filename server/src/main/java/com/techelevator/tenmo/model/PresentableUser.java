package com.techelevator.tenmo.model;

// THESE OBJECTS EXIST TO SEND USERS OUT IN API PAYLOADS WITHOUT SENSITIVE INFORMATION
public class PresentableUser {

    private int id;
    private String username;

    public PresentableUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
