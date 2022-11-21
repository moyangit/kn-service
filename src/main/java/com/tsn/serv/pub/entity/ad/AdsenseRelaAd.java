package com.tsn.serv.pub.entity.ad;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableCharset;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableEngine;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlEngineConstant;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="广告位", description="")
@TableName("v_adsense_rela_ad")
@TableComment("广告位")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class AdsenseRelaAd {
	
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "广告位Id")
    @TableField
    @Unique(value = "ad_index", columns = {"adsense_id","ad_id"})
    private Long adsenseId;
    
    @ApiModelProperty(value = "广告ID")
    @TableField
    private Long adId;
    
    @ApiModelProperty(value = "权重")
    @TableField
    private Integer weight;

}
