package com.tsn.serv.mem.entity.reqData;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.tsn.common.core.norepeat.ReToken;

public class ProxyRechargeDto extends ReToken {

    private String proxyMemId;

    @NotNull
    private String rechargeMemId;

    @NotNull
    private String chargeType;

    @NotNull
    private BigDecimal finalMoney;

    @NotNull
    private String payType;

    public String getProxyMemId() {
        return proxyMemId;
    }

    public void setProxyMemId(String proxyMemId) {
        this.proxyMemId = proxyMemId;
    }

    public String getRechargeMemId() {
        return rechargeMemId;
    }

    public void setRechargeMemId(String rechargeMemId) {
        this.rechargeMemId = rechargeMemId;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public BigDecimal getFinalMoney() {
        return finalMoney;
    }

    public void setFinalMoney(BigDecimal finalMoney) {
        this.finalMoney = finalMoney;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
