package com.peep.contractbak.bean;

/**
 * 手机短信
 * */
public class MsgInfo {
    private String msgTime;
    private String msgCon;
    private String msgTel;
    private String msgType; //接受 发送

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgTel() {
        return msgTel;
    }

    public void setMsgTel(String msgTel) {
        this.msgTel = msgTel;
    }

    public String getMsgCon() {
        return msgCon;
    }

    public void setMsgCon(String msgCon) {
        this.msgCon = msgCon;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
}
