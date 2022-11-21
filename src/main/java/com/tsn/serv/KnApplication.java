package com.tsn.serv;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.tsn.common.core.anno.CacheTypeEum;
import com.tsn.common.core.anno.EnableCusService;
import com.tsn.common.core.anno.EnableRedisMqService;
import com.tsn.common.core.anno.MessgeTypeEum;
import com.tsn.common.db.mysql.anno.EnableActableNoTenantRefreshDB;
import com.tsn.common.utils.web.anno.cors.EnableCors;
import com.tsn.common.utils.web.anno.excep.EnableGExcep;
import com.tsn.common.utils.web.anno.module.EnableCustomScan;
import com.tsn.common.web.EnableJWT;
import com.tsn.common.web.EnableServerAuthV3;

@SpringBootApplication
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AuthApplication.class})})
@EnableCusService(mq = MessgeTypeEum.KAFKA,cache=CacheTypeEum.REDIS)
@EnableCors
@EnableGExcep
@EnableCustomScan
@EnableRedisMqService
@EnableJWT
@EnableServerAuthV3
@EnableActableNoTenantRefreshDB
@MapperScan("com.tsn.serv.*.mapper.*")
@EnableFeignClients(basePackageClasses = KnApplication.class,  basePackages = {"com.tsn.common.security"})
@EnableSwagger2
public class KnApplication {
	
    public static void main(String[] args) {
        try {
			SpringApplication.run(KnApplication.class, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}


