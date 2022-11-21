package com.tsn.serv.pub.entity.ad;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="第三方广告信息表", description="")
//mes_req_record
@TableName("v_advert")
@TableComment("第三方广告信息表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Advert extends BaseEntity{
	
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;

    @ApiModelProperty(value = "广告名称")
    @TableField
    @ColumnType(length = 100)
    private String adName;

    @ApiModelProperty(value = "广告描述")
    @TableField
    @ColumnType(length = 1000)
    private String adDesc;
    
    @ApiModelProperty(value = "广告跳转地址")
    @TableField
    @ColumnType(length = 1000)
    private String skipUrl;

    @ApiModelProperty(value = "排序")
    @TableField
    private Integer seq;
    
    @ApiModelProperty(value = "状态 0 正常， 1禁用")
    @TableField
    private Integer status;
    
    @ApiModelProperty(value = "广告商ID")
    @TableField
    private Long adCustomerId;
    
    @ApiModelProperty(value = "广告商名称")
    @TableField(exist=false)
    @ColumnType(length = 100)
    private String adCustomerName;
    
    @ApiModelProperty(value = "权重")
    @TableField(exist=false)
    private Integer weight;
    
    @TableField(exist=false)
    private Long relaId;
    
    //秒
    @TableField(exist=false)
    private Integer intervalTime;

    @ApiModelProperty(value = "图片base64信息")
    @TableField
    @ColumnType(value=MySqlTypeConstant.MEDIUMBLOB)
    private String adPic;

}