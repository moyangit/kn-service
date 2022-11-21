package com.tsn.serv.common.enm.path;

/**
 * 线路级别
 */
public enum PathGrade {

    // 优质路线
    lev_0("0"),
    // 普通路线
    lev_1("1");

    private String grade;

    PathGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void getGrade(String grade) {
        this.grade = grade;
    }
}
