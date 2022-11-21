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
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="用户留存统计", description="")
//mes_req_record
@TableName("v_statis_user_keep_day")
@TableComment("用户留存统计")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class StatisUserKeepDay extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "yyyyMMdd格式")
    @TableField
    @Unique(columns ={"date_time","type"})
    @ColumnType(length = 20)
    @ColumnComment("yyyyMMdd格式")
    private String dateTime;

    @ApiModelProperty(value = "0总共，1表示zx，2yq")
    @TableField
    @ColumnType(length = 2)
    @ColumnComment("0总共，1表示zx，2yq")
    private String type;
    
    @ApiModelProperty(value = "1日之内，根据datatime时间来")
    @TableField
    @ColumnComment("1日之内，根据datatime时间来")
    private Integer day1;
    
    @ApiModelProperty(value = "3日之内，根据datatime时间来")
    @TableField
    @ColumnComment("2日之内，根据datatime时间来")
    private Integer day2;
    
    @ApiModelProperty(value = "3日之内，根据datatime时间来")
    @TableField
    @ColumnComment("3日之内，根据datatime时间来")
    private Integer day3;
    
    @ApiModelProperty(value = "4日之内，根据datatime时间来")
    @TableField
    @ColumnComment("4日之内，根据datatime时间来")
    private Integer day4;

    @ApiModelProperty(value = "5日之内，根据datatime时间来")
    @TableField
    @ColumnComment("5日之内，根据datatime时间来")
    private Integer day5;
    
    @ApiModelProperty(value = "6日之内，根据datatime时间来")
    @TableField
    @ColumnComment("6日之内，根据datatime时间来")
    private Integer day6;
    
    @ApiModelProperty(value = "7日之内，根据datatime时间来")
    @TableField
    @ColumnComment("7日之内，根据datatime时间来")
    private Integer day7;
    
    @ApiModelProperty(value = "7日之内，根据datatime时间来")
    @TableField
    @ColumnComment("8日之内，根据datatime时间来")
    private Integer day8;
    
    @ApiModelProperty(value = "15日之内，根据datatime时间来")
    @TableField
    @ColumnComment("15日之内，根据datatime时间来")
    private Integer day9;
    
    @ApiModelProperty(value = "15日之内，根据datatime时间来")
    @TableField
    @ColumnComment("15日之内，根据datatime时间来")
    private Integer day10;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day11;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day12;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day13;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day14;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day15;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day16;

    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day17;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day18;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day19;

    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day20;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day21;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day22;

    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day23;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day24;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day25;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day26;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day27;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day28;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day29;
    
    @ApiModelProperty(value = "30日之内，根据datatime时间来")
    @TableField
    @ColumnComment("30日之内，根据datatime时间来")
    private Integer day30;
    

}