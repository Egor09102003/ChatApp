package ru.baryshnikov.chatapp.model;

public class Chatlist {

    private String id;

    public Chatlist(String id) {
        this.id = id;
    }

    public Chatlist() {
        // empty
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
