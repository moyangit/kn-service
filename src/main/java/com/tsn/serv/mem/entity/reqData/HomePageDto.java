package com.tsn.serv.mem.entity.reqData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

public class HomePageDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date statDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String selectType;
    private Map chartData;
    private List chargeData;
    private List customerData;
    private List invitationData;
    private List chartTypeDataList;
    private Map lineChartData;

    public Date getStatDate() { return statDate; }

    public void setStatDate(Date statDate) { this.statDate = statDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getSelectType() { return selectType; }

    public void setSelectType(String selectType) { this.selectType = selectType == null ? null : selectType.trim(); }

    public Map getChartData() { return chartData; }

    public void setChartData(Map chartData) { this.chartData = chartData; }

    public List getChargeData() {
        return chargeData;
    }

    public void setChargeData(List chargeData) {
        this.chargeData = chargeData;
    }

    public List getCustomerData() { return customerData; }

    public void setCustomerData(List customerData) { this.customerData = customerData; }

    public List getInvitationData() {
        return invitationData;
    }

    public void setInvitationData(List invitationData) {
        this.invitationData = invitationData;
    }

    public List getChartTypeDataList() {
        return chartTypeDataList;
    }

    public void setChartTypeDataList(List chartTypeDataList) {
        this.chartTypeDataList = chartTypeDataList;
    }

    public Map getLineChartData() {
        return lineChartData;
    }

    public void setLineChartData(Map lineChartData) {
        this.lineChartData = lineChartData;
    }
}
