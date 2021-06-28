package com.peep.contractbak.bean;

public class FileHelper {
    private String fileAllPath; //文件原完整的地址
    private String fileTempPath; //文件原去掉 /sdcard/0/的地址

    public String getFileAllPath() {
        return fileAllPath;
    }

    public void setFileAllPath(String fileAllPath) {
        this.fileAllPath = fileAllPath;
    }

    public String getFileTempPath() {
        return fileTempPath;
    }

    public void setFileTempPath(String fileTempPath) {
        this.fileTempPath = fileTempPath;
    }
}
