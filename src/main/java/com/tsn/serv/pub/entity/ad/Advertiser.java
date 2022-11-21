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
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="广告用户", description="")
//mes_req_record
@TableName("v_advert_customer")
@TableComment("广告用户")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Advertiser extends BaseEntity{
	
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "登录")
    @TableField
    @ColumnType(length = 100)
    private String loginName;

    @ApiModelProperty(value = "登录密码")
    @TableField
    @ColumnType(length = 30)
    private String passwd;
    
    @ApiModelProperty(value = "广告商名")
    @TableField
    @ColumnType(length = 100)
    private String advertiserName;
    
    @ApiModelProperty(value = "状态 0 正常， 1禁用")
    @TableField
    private Integer status;

}