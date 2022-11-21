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
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="邀请活动时间规则", description="")
//mes_req_record
@TableName("v_activit_rule_cdk")
@TableComment("邀请活动时间规则")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class ActivitRuleTime extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId
    private String id;
    
    @ApiModelProperty(value = "活动类型：cdk")
    @TableField
    @Unique
    @ColumnType(length = 10)
    private String activitType;
    
	@ApiModelProperty(value = "开始时间")
    @TableField
    @ColumnType(MySqlTypeConstant.DATETIME)
    private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
    @TableField
    @ColumnType(MySqlTypeConstant.DATETIME)
    private Date endTime;
}