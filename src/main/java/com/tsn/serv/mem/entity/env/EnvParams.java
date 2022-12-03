package com.tsn.serv.mem.entity.env;

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
@ApiModel(value="环境参数配置", description="")
//mes_req_record
@TableName("v_env_config")
@TableComment("环境参数配置")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class EnvParams {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    //app_store_acc store账号
    @ApiModelProperty(value = "key")
    @TableField
    @Unique
    @ColumnType(length = 100)
    private String keyName;
    
    @ApiModelProperty(value = "key名称")
    @TableField
    @ColumnType(length = 100)
    private String keyDesc;
    
    @ApiModelProperty(value = "值")
    @TableField
    @ColumnType(length = 2000)
    private String value;
    
	@ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @ColumnType(MySqlTypeConstant.DATETIME)
    @ColumnComment("创建时间")
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