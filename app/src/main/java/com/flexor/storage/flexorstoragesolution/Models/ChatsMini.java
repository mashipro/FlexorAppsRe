package com.flexor.storage.flexorstoragesolution.Models;

import com.google.firebase.firestore.ServerTimestamp;

public class ChatsMini {
    private String chatsID;
    private String fromID;
    private String toID;
    private Boolean newMessage;
    private Long timestamp;

    public ChatsMini() {
    }

    @Override
    public String toString() {
        return "ChatsMini{" +
                "chatsID='" + chatsID + '\'' +
                ", fromID='" + fromID + '\'' +
                ", toID='" + toID + '\'' +
                ", newMessage=" + newMessage +
                ", timestamp=" + timestamp +
                '}';
    }

    public ChatsMini(String chatsID, String fromID, String toID, Boolean newMessage, Long timestamp) {
        this.chatsID = chatsID;
        this.fromID = fromID;
        this.toID = toID;
        this.newMessage = newMessage;
        this.timestamp = timestamp;
    }

    public String getChatsID() {
        return chatsID;
    }

    public void setChatsID(String chatsID) {
        this.chatsID = chatsID;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public Boolean getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(Boolean newMessage) {
        this.newMessage = newMessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
