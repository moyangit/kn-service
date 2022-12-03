package com.tsn.serv.mem.entity.mem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("v_mem_info_ext")
@TableComment("用户扩展信息表")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class MemExtInfo extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId(type = IdType.NONE)
    private String id;
    
    @ApiModelProperty(value = "0和空表示没有，1表示已经获取")
    @TableField
    @ColumnComment("0和空表示没有，1表示已经获取")
    private Integer isInvitGetTime;
    
    @ApiModelProperty(value = "参与过邀请活动获得的人数")
    @TableField
    private Integer inivActivitPeopleNum;
    
    @ApiModelProperty(value = "设备名")
    @TableField
    @ColumnType(length = 20)
    private String deviceName;
    
    @ApiModelProperty(value = "设备类型M,W,A,I")
    @TableField
    @ColumnType(length = 2)
    private String deviceType;
    
    @ApiModelProperty(value = "设备号")
    @TableField
    @ColumnType(length = 100)
    @Unique
    @ColumnComment("设备号")
    private String deviceNo;
    
    @ApiModelProperty()
    @TableField
    @ColumnType(length = 200)
    @ColumnComment("渠道点击产生得clickid,用于回传，统计vm金额数据")
    private String channelClickId;
    
    @ApiModelProperty()
    @TableField
    @ColumnType(length = 3000)
    @ColumnComment("渠道点击产生得exId，用于回传给广告平台，统计人数")
    private String channelExId;

}