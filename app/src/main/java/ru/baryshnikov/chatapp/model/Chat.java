package ru.baryshnikov.chatapp.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;

    public Chat(String sender, String receiver, String message, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isSeen;
    }

    public Chat() {
        // empty
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", isseen=" + isseen +
                '}';
    }
}
