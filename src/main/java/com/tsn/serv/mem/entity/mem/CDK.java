package com.tsn.serv.mem.entity.mem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.Index;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="用户扩展信息表", description="")
//mes_req_record
@TableName("v_activit_cdk")
@TableComment("用户扩展信息表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class CDK {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "CDK号")
    @TableField
    @Unique
    @ColumnType(length = 100)
    private String cdkNo;
    
    @ApiModelProperty(value = "cdk类型 day， week  month，")
    @TableField
    @ColumnType(length = 10)
    private String cdkType;
    
    @ApiModelProperty(value = "奖励时长，单位分钟")
    @TableField
    private Integer duration;
    
    @ApiModelProperty(value = "会员ID")
    @Index
    @TableField
    @ColumnType(length = 50)
    private String userId;
    
    @ApiModelProperty(value = "用户名")
    @Index
    @TableField
    @ColumnType(length = 50)
    private String userName;
    
    @ApiModelProperty(value = "兑换人ID")
    @Index
    @TableField
    @ColumnType(length = 50)
    private String convertUserId;
    
    @ApiModelProperty(value = "兑换人手机号")
    @Index
    @TableField
    @ColumnType(length = 50)
    private String convertUserName;
    
    @ApiModelProperty(value = "auto 系统，manual")
    @TableField
    @ColumnType(length = 10)
    private String sourceType;
    
    @ApiModelProperty(value = "状态0表示 未兑换，1表示是已兑换")
    @TableField(fill = FieldFill.INSERT)
    @ColumnComment("状态")
    private Integer status;
    
    @ApiModelProperty(value = "是否发送msg")
    @TableField(exist = false)
    private int msg;
    
	@ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @ColumnType(MySqlTypeConstant.DATETIME)
    @ColumnComment("创建时间")
	@Index
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ColumnType(MySqlTypeConstant.DATETIME)
    @ColumnComment("更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "乐观锁版本号")
    @TableField(fill = FieldFill.INSERT)
    @ColumnComment("乐观锁版本号")
    private Integer version;
}