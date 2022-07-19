package com.example.ais_store.Packet;

import com.example.ais_store.Message.Message;
import com.example.ais_store.Utils.CRC16;

import java.nio.ByteBuffer;

public class Packet {
    public static final byte MAGIC_BYTE = 0x13;
    private static long freePacketId = 0;

    public static final int B_MAGIC_LENGTH = Byte.BYTES;
    public static final int B_SRC_LENGTH = Byte.BYTES;
    public static final int B_PKT_ID_LENGTH = Long.BYTES;
    public static final int W_LEN_LENGTH = Integer.BYTES;
    public static final int W_CRC16_LENGTH = Short.BYTES;
    public static final int M_W_CRC16_LENGTH = Short.BYTES;

    private final byte[] bytesOfPacket;

    public static synchronized long getFreePacketId() {
        return freePacketId++;
    }

    public Packet(byte bSrc, Message message) {
        long bPktId = freePacketId;
        ++freePacketId;

        int wLen = message.getBytes().length;

        // setting crc16
        ByteBuffer bf = ByteBuffer.allocate(B_MAGIC_LENGTH + B_SRC_LENGTH + B_PKT_ID_LENGTH + W_LEN_LENGTH);
        bf.put(MAGIC_BYTE);
        bf.put(bSrc);
        bf.putLong(bPktId);
        bf.putInt(wLen);

        short wCrc16 = CRC16.Count(bf.array());

        Message bMsg = message;

        // setting crc16 of bMsg
        short m_wCrc16 = CRC16.Count(bMsg.getBytes());

        // setting bytes of the packet
        int fullLength = B_MAGIC_LENGTH + B_SRC_LENGTH + B_PKT_ID_LENGTH + W_LEN_LENGTH
                + W_CRC16_LENGTH + wLen + M_W_CRC16_LENGTH;

        bf = ByteBuffer.allocate(fullLength);
        bf.put(MAGIC_BYTE);
        bf.put(bSrc);
        bf.putLong(bPktId);
        bf.putInt(wLen);
        bf.putShort(wCrc16);
        bf.put(bMsg.getBytes());
        bf.putShort(m_wCrc16);

        this.bytesOfPacket = bf.array();
    }

    public Packet(byte bSrc, long bPktId, Message bMsg) {
        int wLen = bMsg.getBytes().length;

        // setting crc16
        ByteBuffer bf = ByteBuffer.allocate(B_MAGIC_LENGTH + B_SRC_LENGTH + B_PKT_ID_LENGTH + W_LEN_LENGTH);
        bf.put(MAGIC_BYTE);
        bf.put(bSrc);
        bf.putLong(bPktId);
        bf.putInt(wLen);

        short wCrc16 = CRC16.Count(bf.array());

        // setting crc16 of bMsg
        short m_wCrc16 = CRC16.Count(bMsg.getBytes());

        // setting bytes of the packet
        int fullLength = B_MAGIC_LENGTH + B_SRC_LENGTH + B_PKT_ID_LENGTH + W_LEN_LENGTH
                + W_CRC16_LENGTH + wLen + M_W_CRC16_LENGTH;

        bf = ByteBuffer.allocate(fullLength);
        bf.put(MAGIC_BYTE);
        bf.put(bSrc);
        bf.putLong(bPktId);
        bf.putInt(wLen);
        bf.putShort(wCrc16);
        bf.put(bMsg.getBytes());
        bf.putShort(m_wCrc16);

        bytesOfPacket = bf.array();
    }

    public byte[] getBytes() {
        return bytesOfPacket;
    }
}
