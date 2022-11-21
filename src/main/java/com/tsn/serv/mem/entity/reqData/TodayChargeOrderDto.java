package com.tsn.serv.mem.entity.reqData;

import java.util.List;
import java.util.Map;

public class TodayChargeOrderDto {

    private List chartTypeDataList;
    private Map lineChartData;

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
