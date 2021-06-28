package com.peep.contractbak.bean;

import java.io.File;

public class FileSelectHelper {
    private File file;
    private boolean selectFlag;

    public FileSelectHelper(File file,boolean selectFlag){
        this.file = file;
        this.selectFlag = selectFlag;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }
}
