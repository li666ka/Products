package com.example.ais_store.Message;

public class MessageInfo {
    public final int cType; // command id
    public final int bUserId; // sender
    public final byte[] message; // info

    public MessageInfo(int cType, int bUserId, byte[] message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }
}
