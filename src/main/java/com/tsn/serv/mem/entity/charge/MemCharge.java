package com.tsn.serv.mem.entity.charge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="系统配置表", description="")
//mes_req_record
@TableName("v_mem_charge")
@TableComment("系统配置表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class MemCharge {
	
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId(type = IdType.NONE)
    private String chargeId;

    @ApiModelProperty(value = "会员类型 01:普通会员;  02:高级会员;  03:企业会员;  04:代理")
    @TableField
    @ColumnType(length = 10)
    @ColumnComment("会员类型")
    private String memType;

    @ApiModelProperty(value = "资费类型 10:月卡;   11:季卡;   12:半年卡;   13:年卡; 14：永久")
    @TableField
    @ColumnType(length = 10)
    private String chargeType;

    @ApiModelProperty(value = "0表示非自然月，1表示自然月，默认为0")
    @TableField
    @ColumnType(value = MySqlTypeConstant.TINYINT)
    private Byte chargeRule;

    @ApiModelProperty(value = "金额")
    @TableField
    private BigDecimal chargeMoney;

    @ApiModelProperty(value = "折扣10-100，计算乘以0.01,默认100")
    @TableField
    private Integer discount;

    @ApiModelProperty(value = "资费类型 10:月卡;   11:季卡;   12:半年卡;   13:年卡; 14：永久")
    @TableField
    @ColumnType(length = 10)
    private String operaUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField
    private Date updateTime;
    
    @ApiModelProperty(value = "更新时间")
    @TableField(exist = false)
    private BigDecimal finalMoney;
    
    @ApiModelProperty(value = "美元汇率")
    @TableField
    private BigDecimal usdRateVal;

    public BigDecimal getUsdRateVal() {
		return usdRateVal;
	}

	public void setUsdRateVal(BigDecimal usdRateVal) {
		this.usdRateVal = usdRateVal;
	}

	@Override
	public String toString() {
		return "MemCharge [chargeId=" + chargeId + ", memType=" + memType
				+ ", chargeType=" + chargeType + ", chargeRule=" + chargeRule
				+ ", chargeMoney=" + chargeMoney + ", discount=" + discount
				+ ", operaUserId=" + operaUserId + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType == null ? null : memType.trim();
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType == null ? null : chargeType.trim();
    }

    public Byte getChargeRule() {
        return chargeRule;
    }

    public void setChargeRule(Byte chargeRule) {
        this.chargeRule = chargeRule;
    }

    public BigDecimal getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(BigDecimal chargeMoney) {
        this.chargeMoney = chargeMoney;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getOperaUserId() {
        return operaUserId;
    }

    public void setOperaUserId(String operaUserId) {
        this.operaUserId = operaUserId == null ? null : operaUserId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public BigDecimal getFinalMoney() {
		return finalMoney;
	}

	public void setFinalMoney(BigDecimal finalMoney) {
		this.finalMoney = finalMoney;
	}
}