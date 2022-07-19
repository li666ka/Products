package com.example.ais_store.Packet;

import com.example.ais_store.Message.MessageInfo;

import java.nio.charset.StandardCharsets;

public class PacketInfo {
    public final byte bSrc; // unique number of client application
    public final long bPktId; // number of packet
    public final int wLen; // length of packet
    public final MessageInfo bMsg; // message

    public PacketInfo(byte bSrc, long bPktId, int wLen, MessageInfo bMsg) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.wLen = wLen;
        this.bMsg = bMsg;
    }

    public void show() {
        System.out.println("\tPacketInfo:" + "\n"
                + "\t\tbSrc:\t\t" + bSrc + "\n"
                + "\t\tbPktId:\t\t" + bPktId + "\n"
                + "\t\twLen:\t\t" + wLen + "\n"
                + "\t\tMessageInfo:" + "\n"
                + "\t\t\tcType:\t\t" + bMsg.cType + "\n"
                + "\t\t\tbUserId:\t" + bMsg.bUserId + "\n"
                + "\t\t\tmessage:\t" + new String(bMsg.message, StandardCharsets.UTF_8));
    }
}
