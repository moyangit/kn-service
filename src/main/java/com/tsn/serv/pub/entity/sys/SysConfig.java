package com.tsn.serv.pub.entity.sys;

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
@ApiModel(value="系统配置表", description="")
//mes_req_record
@TableName("v_sys_config")
@TableComment("系统配置表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class SysConfig extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private String id;
    
    @ApiModelProperty(value = "key")
    @TableField
    @Unique
    @ColumnType(length = 100)
    @ColumnComment("配置key")
    private String confKey;
    
    @ApiModelProperty(value = "key")
    @TableField
    @Unique
    @ColumnType(length = 100)
    @ColumnComment("name")
    private String confKeyName;
    
    @ApiModelProperty(value = "key对应的值")
    @TableField
    @Unique
    @ColumnType(length = 100)
    @ColumnComment("配置值")
    private String confVal;
    
}