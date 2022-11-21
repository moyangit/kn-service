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
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="广告位", description="")
@TableName("v_adsense")
@TableComment("广告位")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class Adsense {
	
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "广告位code")
    @Unique
    @TableField
    @ColumnType(length = 100)
    private String adsenseCode;
    
    @ApiModelProperty(value = "广告位名称")
    @TableField
    @ColumnType(length = 100)
    private String adsenseName;
    
    @ApiModelProperty(value = "应用类型 黑豹，快加速 或者")
    @TableField
    @ColumnType(length = 100)
    private String appType;
    
    @ApiModelProperty(value = "状态 0 正常， 1禁用")
    @TableField
    private Integer status;
    
    @ApiModelProperty(value = "广告下次显示的间隔时间，单位秒")
    @TableField
    private Integer intervalTime;
    
    @ApiModelProperty(value = "展示模式 随机 或者 权重， random， weight")
    @TableField
    @ColumnType(length = 10)
    private String mode;
    
    

}
