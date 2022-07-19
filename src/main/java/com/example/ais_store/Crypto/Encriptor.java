package com.example.ais_store.Crypto;

import com.example.ais_store.Message.Message;
import com.example.ais_store.Packet.Packet;
import com.example.ais_store.Packet.PacketInfo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Arrays;

public class Encriptor {
    public static final Key KEY;
    private static final Cipher CIPHER;

    static {
        try {
            KEY = KeyGenerator.getInstance("AES").generateKey();
            CIPHER = Cipher.getInstance("AES");
            CIPHER.init(Cipher.ENCRYPT_MODE, KEY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Packet encrypt(PacketInfo packetInfo) throws IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedMessage = CIPHER.doFinal(packetInfo.bMsg.message);

        return new Packet(
                packetInfo.bSrc,
                packetInfo.bPktId,
                new Message(
                        packetInfo.bMsg.cType,
                        packetInfo.bMsg.bUserId,
                        encryptedMessage));
    }
}
