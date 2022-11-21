package com.tsn.serv.mem.entity.reqData;

import javax.validation.constraints.NotNull;

public class ProxyBingdingDto {

    @NotNull
    private String proxyMemId;

    @NotNull
    private String bdMemId;

    public String getProxyMemId() {
        return proxyMemId;
    }

    public void setProxyMemId(String proxyMemId) {
        this.proxyMemId = proxyMemId;
    }

    public String getBdMemId() {
        return bdMemId;
    }

    public void setBdMemId(String bdMemId) {
        this.bdMemId = bdMemId;
    }
}
