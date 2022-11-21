package com.tsn.serv.mem.entity.order;

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
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="用户扩展信息表", description="")
//mes_req_record
@TableName("v_mem_order_rebate_wait")
@TableComment("用户单独待返利表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class RebateWaitOrder extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId
    private String id;
    
    @ApiModelProperty(value = "邀请人Id")
    @TableField
    @ColumnType(length = 40)
    private String invitMemId;
    
    @ApiModelProperty(value = "被邀请人订单")
    @TableField
    @ColumnType(length = 40)
    private String memId;
    
    @ApiModelProperty(value = "设备号")
    @TableField
    @Unique
    @ColumnType(length = 100)
    @ColumnComment("被邀请人订单数")
    private String memOrderNo;
    
    @ApiModelProperty(value = "0 未处理， 1已处理")
    @TableField(fill = FieldFill.INSERT)
    private Integer status;

}