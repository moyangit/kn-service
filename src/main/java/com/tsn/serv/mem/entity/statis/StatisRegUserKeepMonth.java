package com.tsn.serv.mem.entity.statis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.tsn.common.db.mysql.base.TenantEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="注册用户留存统计", description="")
//mes_req_record
@TableName("v_statis_reg_user_keep_month")
@TableComment("用户留存统计")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class StatisRegUserKeepMonth extends TenantEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "yyyyMM")
    @TableField
    @Unique(columns ={"date_time","type"})
    @ColumnType(length = 20)
    @ColumnComment("yyyyMM")
    private String dateTime;

    @ApiModelProperty(value = "0表示留存")
    @TableField
    @ColumnType(length = 2)
    @ColumnComment("0表示留存")
    private String type;

    @ApiModelProperty(value = "1日之内，根据datatime时间来")
    @TableField
    @ColumnComment("1日之内，根据datatime时间来")
    private Integer one;
    
    @ApiModelProperty(value = "2日之内，根据datatime时间来")
    @TableField
    @ColumnComment("2月之内，根据datatime时间来")
    private Integer two;

    @ApiModelProperty(value = "3日之内，根据datatime时间来")
    @TableField
    @ColumnComment("3月之内，根据datatime时间来")
    private Integer three;
    
    @ApiModelProperty(value = "4日之内，根据datatime时间来")
    @TableField
    @ColumnComment("4月之内，根据datatime时间来")
    private Integer four;
    
    @ApiModelProperty(value = "5日之内，根据datatime时间来")
    @TableField
    @ColumnComment("5月之内，根据datatime时间来")
    private Integer five;
    
    @ApiModelProperty(value = "6日之内，根据datatime时间来")
    @TableField
    @ColumnComment("6月之内，根据datatime时间来")
    private Integer six;
    
    @ApiModelProperty(value = "7日之内，根据datatime时间来")
    @TableField
    @ColumnComment("7月之内，根据datatime时间来")
    private Integer seven;
    
    @ApiModelProperty(value = "8日之内，根据datatime时间来")
    @TableField
    @ColumnComment("8月之内，根据datatime时间来")
    private Integer eight;
    
    @ApiModelProperty(value = "9日之内，根据datatime时间来")
    @TableField
    @ColumnComment("9月之内，根据datatime时间来")
    private Integer nine;
    
    @ApiModelProperty(value = "10日之内，根据datatime时间来")
    @TableField
    @ColumnComment("10月之内，根据datatime时间来")
    private Integer ten;
    
    @ApiModelProperty(value = "11日之内，根据datatime时间来")
    @TableField
    @ColumnComment("11月之内，根据datatime时间来")
    private Integer eleven;
    
    @ApiModelProperty(value = "12日之内，根据datatime时间来")
    @TableField
    @ColumnComment("12月之内，根据datatime时间来")
    private Integer twelve;
    

}