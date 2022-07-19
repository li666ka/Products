package com.example.ais_store.TCP;

import com.example.ais_store.Crypto.*;
import com.example.ais_store.Message.*;
import com.example.ais_store.Packet.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientTCP {
    private static byte freeId = 0;
    public final byte id;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public ClientTCP() {
        id = freeId++;
    }

    public void startConnection(int port) throws IOException {
        clientSocket = new Socket("localhost", port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    public PacketInfo send(int command, String parameters) throws Exception {
        // creating packet
        PacketInfo sendPacketInfo = new PacketInfo(
                this.id,
                Packet.getFreePacketId(),
                Message.B_USER_ID_LENGTH + Message.C_TYPE_LENGTH + parameters.getBytes().length,
                new MessageInfo(
                        command,
                        4,
                        parameters.getBytes()));

        // creating encrypted packet for sending
        Packet sendPacket = Encriptor.encrypt(sendPacketInfo);

        // sending
        out.write(sendPacket.getBytes());

        // receiving
        byte b;
        while (true) {
            try {
                if ((b = in.readByte()) == Packet.MAGIC_BYTE) {
                    byte bSrc = in.readByte();
                    long bPktId = in.readLong();
                    int wLen = in.readInt();
                    short wCrc16 = in.readShort();
                    byte[] bMsg = new byte[wLen];
                    in.read(bMsg, 0, wLen);
                    short m_wCrc16 = in.readShort();

                    ByteBuffer buf = ByteBuffer.allocate(
                            Packet.B_MAGIC_LENGTH +
                                    Packet.B_PKT_ID_LENGTH +
                                    Packet.B_SRC_LENGTH +
                                    Packet.W_LEN_LENGTH +
                                    Packet.W_CRC16_LENGTH +
                                    wLen +
                                    Packet.M_W_CRC16_LENGTH);
                    buf.put(b).put(bSrc).putLong(bPktId).putInt(wLen).putShort(wCrc16).put(bMsg).putShort(m_wCrc16);

                    PacketInfo response = Decriptor.decript(buf.array());

                    System.out.println("Packet Response");
                    response.show();
                    return response;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
