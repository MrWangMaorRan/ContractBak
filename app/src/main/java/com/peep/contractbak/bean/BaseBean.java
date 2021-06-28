package com.peep.contractbak.bean;

/**
 * socket通信基础bean
 * */
public class BaseBean {
    private int state; //0为正常 1不正常
    private String data; //数据信息
    private int msgFlag;
    private String marker; //标识

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(int msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}
