package com.tsn.serv.mem.mapper.charge;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.charge.MemCharge;

public interface MemChargeMapper extends IBaseMapper<MemCharge>{
	
    /**
     * 主键查询
     * @param id id
     * @return 用户信息
     */
    List<MemCharge> selectMemChargeByMemType(String memType);
    
    MemCharge selectMemChargeOneByType(@Param("memType") String memType, @Param("chargeType") String chargeType);

    List<MemCharge> getMenChargeList(Page page);
}