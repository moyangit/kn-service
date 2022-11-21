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
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;
import com.tsn.common.db.mysql.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="用户地理信息来源", description="")
@TableName("v_mem_source")
@TableComment("用户地理信息来源")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class MemSource extends BaseEntity {
	
	//所有的主键都用名称“id”, 类型用Long类型
    @ApiModelProperty(value = "Id")
    @ColumnType(length = 40)
    @TableId(type = IdType.NONE)
    private String id;
    
    @ApiModelProperty(value = "ip")
    @TableField
    @ColumnType(length = 100)
    @ColumnComment("ip")
    private String cip;
    
    @ApiModelProperty(value = "省市区id")
    @TableField
    @ColumnType(length = 20)
    @ColumnComment("省市区id")
    private String cid;
    
    @ApiModelProperty(value = "省市区名称")
    @TableField
    @ColumnType(length = 50)
    @ColumnComment("省市区名称")
    private String cname;

}