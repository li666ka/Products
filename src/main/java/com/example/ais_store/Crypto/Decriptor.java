package com.example.ais_store.Crypto;

import com.example.ais_store.Message.*;
import com.example.ais_store.Packet.*;
import com.example.ais_store.Utils.CRC16;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;


public class Decriptor {

    private static final Cipher DECIPHER;

    static {
        try {
            DECIPHER = Cipher.getInstance("AES");
            DECIPHER.init(Cipher.DECRYPT_MODE, Encriptor.KEY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PacketInfo decript(byte[] bytes) throws Exception {
        PacketInfo InfoOfEncryptedPacket = getPacketInfo(bytes);
        byte[] decriptedMessage = DECIPHER.doFinal(InfoOfEncryptedPacket.bMsg.message);

        return new PacketInfo(
                InfoOfEncryptedPacket.bSrc,
                InfoOfEncryptedPacket.bPktId,
                InfoOfEncryptedPacket.wLen,
                new MessageInfo(
                        InfoOfEncryptedPacket.bMsg.cType,
                        InfoOfEncryptedPacket.bMsg.bUserId,
                        decriptedMessage));
    }

    private static PacketInfo getPacketInfo(byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte bMagic = buffer.get();

        if (bMagic != Packet.MAGIC_BYTE) {
            throw new Exception("Incorrect start of packet: " + bMagic);
        }

        byte bSrc = buffer.get();
        long bPktId = buffer.getLong();
        int wLen = buffer.getInt();
        short wCrc16 = buffer.getShort();

        byte[] bytesBeforeMessage =
                ByteBuffer.allocate(Packet.B_MAGIC_LENGTH + Packet.B_SRC_LENGTH
                                + Packet.B_PKT_ID_LENGTH + Packet.W_LEN_LENGTH)
                        .put(Packet.MAGIC_BYTE).put(bSrc).putLong(bPktId).putInt(wLen).array();

        if (wCrc16 != CRC16.Count(bytesBeforeMessage)) {
            throw new Exception("Incorrect Utils.CRC16 at start");
        }

        int cType = buffer.getInt();
        int bUserId = buffer.getInt();
        byte[] message = new byte[wLen - 8];
        buffer.get(message, 0, message.length);

        Message bMsg = new Message(cType, bUserId, message);

        short m_wCrc16 = buffer.getShort();
        if (m_wCrc16 != CRC16.Count(bMsg.getBytes())) {
            throw new Exception("Incorrect Utils.CRC16 of message");
        }

        return new PacketInfo(
                bSrc,
                bPktId,
                wLen,
                new MessageInfo(
                        cType,
                        bUserId,
                        message));
    }
}
