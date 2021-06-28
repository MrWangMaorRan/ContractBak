package com.peep.contractbak.bean;

import java.io.File;

public class CalendarSelectHelper {
    private CalendarBean calendarBean;
    private boolean selectFlag;

    public CalendarSelectHelper(CalendarBean calendarBean, boolean selectFlag){
        this.calendarBean = calendarBean;
        this.selectFlag = selectFlag;
    }

    public CalendarBean getCalendarBean() {
        return calendarBean;
    }

    public void setCalendarBean(CalendarBean calendarBean) {
        this.calendarBean = calendarBean;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }
}
