package com.tsn.serv.pub.entity.emailuser;

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

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="邮件推广用户", description="")
@TableName("v_email_user")
@TableComment("")
@TableCharset(MySqlCharsetConstant.UTF8)
@TableEngine(MySqlEngineConstant.InnoDB)
public class EmailUser {
	
    @ApiModelProperty(value = "Id")
    @TableId
    private Long id;
    
    @ApiModelProperty(value = "")
    @TableField
    @ColumnType(length = 100)
    private String email;
    
    

}
