package com.tsn.serv.mem.entity.activit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="邀请活动规则，累计充值人数奖励", description="")
//mes_req_record
@TableName("v_activit_rule_invit")
@TableComment("邀请活动规则")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class ActivitRuleInvit extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId
    private String id;
    
    @ApiModelProperty(value = "充值人数")
    @TableField
    @ColumnType
    private Integer peopleNum;
    
    @ApiModelProperty(value = "cdk类型，day，week，month")
    @TableField
    @ColumnType(length=10)
    private String dayCdkType;
    
    @ApiModelProperty(value = "cdk数量")
    @TableField
    private Integer dayCdkNum;
    
    @ApiModelProperty(value = "cdk类型，day，week，month")
    @TableField
    @ColumnType(length=10)
    private String weekCdkType;
    
    @ApiModelProperty(value = "cdk数量")
    @TableField
    private Integer weekCdkNum;
    
    @ApiModelProperty(value = "cdk类型，day，week，month")
    @TableField
    @ColumnType(length=10)
    private String monthCdkType;
    
    @ApiModelProperty(value = "cdk数量")
    @TableField
    private Integer monthCdkNum;
    
    @ApiModelProperty(value = "奖励时长")
    @TableField
    private Integer rewardDuration;
    
/*    @ApiModelProperty(value = "奖励时长，单位分钟")
    @TableField
    private Integer duration;*/
    
    @ApiModelProperty(value = "如果为0表示不做操作，")
    @TableField
    private Integer proxyRebate;
    
	@ApiModelProperty(value = "开始时间")
    @TableField
    @ColumnType(MySqlTypeConstant.DATETIME)
    private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
    @TableField
    @ColumnType(MySqlTypeConstant.DATETIME)
    private Date endTime;
}