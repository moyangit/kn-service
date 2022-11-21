package com.tsn.serv.mem.entity.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="站内通知表", description="")
//mes_req_record
@TableName("v_app_msg")
@TableComment("站内通知表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class AppMsg extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId
    private String id;
    
    @ApiModelProperty(value = "user用户消息，sys系统消息")
    @TableField
    @ColumnType(length = 10)
    private String msgType;
    
    @ApiModelProperty(value = "标题")
    @TableField
    @ColumnType(length = 100)
    private String msgTitle;
    
    @ApiModelProperty(value = "内容")
    @TableField
    @ColumnType(length = 2000)
    private String msgContent;
    
    @ApiModelProperty(value = "发送着Id，默认是system")
    @TableField
    @ColumnType(length = 40)
    private String senderId;
    
    @ApiModelProperty(value = "发送着名称，system")
    @TableField
    @ColumnType(length = 40)
    private String senderName;
    
    @ApiModelProperty(value = "发送着Id")
    @TableField
    @ColumnType(length = 40)
    private String receiverId;
    
    @ApiModelProperty(value = "发送着名称")
    @TableField
    @ColumnType(length = 40)
    private String receiverName;
    
    @ApiModelProperty(value = "发送着名称")
    @TableField
    @ColumnType(length = 40)
    private String receiverPhone;
    
    @ApiModelProperty(value = "0 不紧急，1紧急")
    @TableField
    @ColumnComment("紧急标识")
    private Integer urgent;
    
    @ApiModelProperty(value = "状态0表示 wei du ，1表示是yidu ")
    @TableField(fill = FieldFill.INSERT)
    @ColumnComment("状态")
    private Integer status;
}