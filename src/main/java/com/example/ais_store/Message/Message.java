package com.example.ais_store.Message;

import java.nio.ByteBuffer;

public class Message {
    public static final int C_TYPE_LENGTH = Integer.BYTES;
    public static final int B_USER_ID_LENGTH = Integer.BYTES;

    private final byte[] bytesOfPacket;

    public Message(int cType, int bUserId, byte[] message) {
        int size = C_TYPE_LENGTH + B_USER_ID_LENGTH + message.length;
        bytesOfPacket = ByteBuffer.allocate(size).putInt(cType).putInt(bUserId).put(message).array();
    }

    public byte[] getBytes() {
        return bytesOfPacket;
    }
}
