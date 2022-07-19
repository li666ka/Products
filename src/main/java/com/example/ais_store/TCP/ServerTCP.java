package com.example.ais_store.TCP;

import com.example.ais_store.Crypto.Decriptor;
import com.example.ais_store.Crypto.Encriptor;
import com.example.ais_store.Packet.Packet;
import com.example.ais_store.Packet.PacketInfo;
import com.example.ais_store.Processor.Processor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerTCP implements Runnable {
    private ServerSocket serverSocket;
    private int port;

    public ServerTCP(int port) {
        this.port = port;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        for (int i = 0; i < 20; ++i) {
            new Thread(new ClientHandler(serverSocket.accept())).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    @Override
    public void run() {
        try {
            start(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataOutputStream out;
        private DataInputStream in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new DataOutputStream(clientSocket.getOutputStream());
                in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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

                        PacketInfo receivedPacketInfo = Decriptor.decript(buf.array());
                        System.out.println("Packet was received");
                        receivedPacketInfo.show();

                        // creating packet info of response
                        PacketInfo responsePacketInfo = Processor.process(receivedPacketInfo);

                        // creating encrypted packet of response
                        Packet responsePacket = Encriptor.encrypt(responsePacketInfo);

                        // sending
                        out.write(responsePacket.getBytes());
                    }
                } catch (Exception e) {
                    if (e.getClass() != EOFException.class) {
                        throw new RuntimeException(e);
                    }
                }


            }
        }
    }
}