package com.tsn.serv.pub.entity.access;

public class AccessHisDay {
    private String accessDayId;

    /**
     * 日期：yyyymmdd
     */
    private String day;

    /**
     * 小时：24小时制
     */
    private String hour;

    /**
     * 访问路径
     */
    private String accessPath;

    /**
     * 访问次数
     */
    private Integer accessNum;

    private Integer downNum;

    public String getAccessDayId() {
        return accessDayId;
    }

    public void setAccessDayId(String accessDayId) {
        this.accessDayId = accessDayId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getAccessPath() {
        return accessPath;
    }

    public void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }

    public Integer getAccessNum() {
        return accessNum;
    }

    public void setAccessNum(Integer accessNum) {
        this.accessNum = accessNum;
    }

    public Integer getDownNum() {
        return downNum;
    }

    public void setDownNum(Integer downNum) {
        this.downNum = downNum;
    }

    @Override
    public String toString() {
        return "AccessHisDay{" +
                "accessDayId='" + accessDayId + '\'' +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", accessPath='" + accessPath + '\'' +
                ", accessNum=" + accessNum +
                '}';
    }
}
