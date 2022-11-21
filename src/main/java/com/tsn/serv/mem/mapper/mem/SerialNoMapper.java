package com.tsn.serv.mem.mapper.mem;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsn.serv.mem.entity.mem.SerialNo;

/**
 * <p>
 * 用户留存统计 Mapper 接口
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
public interface SerialNoMapper extends BaseMapper<SerialNo> {
	
	@Insert("<script>"
            + "insert ignore into `v_mem_serial_no`"
            + "(`id`, `serial_no`) "
            + "values "
            + "<foreach collection=\"list\" item=\"item\" separator=\",\">"
            + "(#{item.id}, #{item.serialNo})"
            + "</foreach>"
            + " on duplicate key update serial_no=values(serial_no)"
            + "</script>")
	Long insertBatchSerialNo(@Param("list") List<SerialNo> serialNoList);

}
