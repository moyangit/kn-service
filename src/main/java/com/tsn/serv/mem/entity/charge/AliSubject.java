package com.tsn.serv.mem.entity.charge;

public class AliSubject {
    private Integer id;

    /**
     * 支付宝支商品信息名称
     */
    private String subjectName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "AliSubject{" +
                "id=" + id +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}