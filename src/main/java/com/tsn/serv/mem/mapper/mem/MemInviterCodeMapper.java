package com.tsn.serv.mem.mapper.mem;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.serv.mem.entity.mem.MemInviterCode;

public interface MemInviterCodeMapper extends IBaseMapper<MemInviterCode> {
	int delete(Integer id);

	int insert(MemInviterCode memInviterCode);

	int insertDynamic(MemInviterCode memInviterCode);

	int updateDynamic(MemInviterCode memInviterCode);

	int update(MemInviterCode memInviterCode);

	MemInviterCode selectById(Integer id);

	MemInviterCode getInviterCode();

}