package com.tsn.serv.mem.service.source;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.source.MemSourceInviter;
import com.tsn.serv.mem.mapper.source.MemSourceInviterMapper;

@Service
public class MemSourceInviterService {

	@Autowired
	private MemSourceInviterMapper memSourceInviterMapper;

	public void getListByPage(Page page) {
		List<MemSourceInviter> memSourceInviterList = memSourceInviterMapper.getListByPage(page);
		page.setData(memSourceInviterList);
	}

	public MemSourceInviter getDetailsByPath(String sourcePath) {
		// base64解密
		// String sourcePathTo = Base64Utils.decodeToString(sourcePath);
		return memSourceInviterMapper.getDetailsByPath(sourcePath);
	}

	public Response<?> addSourceInviter(MemSourceInviter sourceInviter) {
		MemSourceInviter memSourceInviter = memSourceInviterMapper.selectByInviterCode(sourceInviter.getInviterCode());
		if (memSourceInviter != null) {
			return Response.retn("000404", "邀请码已存在");
		}
		sourceInviter.setNum(new Integer(0));
		memSourceInviterMapper.insertSelective(sourceInviter);
		return Response.ok();
	}

	public Response<?> deleteSourceInviter(MemSourceInviter sourceInviter) {
		memSourceInviterMapper.deleteByPrimaryKey(sourceInviter.getId());
		return Response.ok();
	}
}
