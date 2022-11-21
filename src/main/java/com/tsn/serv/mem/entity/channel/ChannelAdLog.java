package com.tsn.serv.mem.entity.channel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
@ApiModel(value="渠道广告日志", description="")
@TableName("v_mem_channel_ad_log")
@TableComment("渠道广告日志")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class ChannelAdLog extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "用户Id")
    @TableField
    @ColumnType(length = 40)
    private String memId;
    
    @ApiModelProperty(value = "会员类型")
    @TableField
    @ColumnType(length = 4)
    private String memType;
    
    @ApiModelProperty(value = "渠道")
    @TableField
    @ColumnType(length = 40)
    private String channelCode;
    
    @ApiModelProperty(value = "回调类型")
    @TableField
    @ColumnType(length = 20)
    private String callbackType;
    
    @ApiModelProperty(value = "clickid或者channelExId")
    @TableField
    @ColumnType(length = 2000)
    private String callbackUrl;
    
    @ApiModelProperty(value = "回调返回结果")
    @TableField
    @ColumnType(length = 4000)
    private String callbackResult;
    
    @ApiModelProperty(value = "状态")
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    

}