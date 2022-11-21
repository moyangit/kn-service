package com.tsn.serv.pub.service.complaint;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.pub.entity.complaint.Complaint;
import com.tsn.serv.pub.entity.complaint.ComplaintType;
import com.tsn.serv.pub.mapper.complaint.ComplaintMapper;
import com.tsn.serv.pub.mapper.complaint.ComplaintTypeMapper;
import com.tsn.serv.pub.service.mem.MemFeignService;

@Service
public class ComplaintService {

	@Autowired
	private ComplaintTypeMapper complaintTypeMapper;

	@Autowired
	private ComplaintMapper complaintMapper;

	@Autowired
	private MemFeignService memService;

	public List<ComplaintType> selectAllComplaintType(){
		List<ComplaintType> complaintTypeList = complaintTypeMapper.selectAllComplaintType();
		return complaintTypeList;
	}

	public void getComplaintPage(Page page) {
		List<Map<String, Object>> complaintList = complaintMapper.getComplaintPage(page);
		page.setData(complaintList);
	}

	public void addComplaint(Complaint complaint) {
		// 当用户手机号为null时
		if (StringUtils.isEmpty(complaint.getMemPhone())){
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "memPhone error");
		}
		MemInfo memInfo = memService.selectUserByPhone(complaint.getMemPhone());
		if (memInfo == null) {
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "memPhone not isExists");
		}
		complaint.setMemId(memInfo.getMemId());
		complaint.setMemPhone(memInfo.getMemPhone());
		complaint.setMemName(memInfo.getMemNickName());
		complaint.setCompStatus("0");
		complaint.setCreateTime(new Date());
		complaintMapper.insertDynamic(complaint);
	}

	public void upComplaint(Complaint complaint) {
		complaint.setUpdateTime(new Date());
		complaintMapper.updateDynamic(complaint);
	}

	public void getComplaintTypePage(Page page) {
		List<ComplaintType> complaintTypeList = complaintTypeMapper.getComplaintTypePage(page);
		page.setData(complaintTypeList);
	}

	public void addComplaintType(ComplaintType complaintType) {
		complaintType.setStatus("0");
		complaintTypeMapper.insertDynamic(complaintType);
	}

	public void upComplaintType(ComplaintType complaintType) {
		complaintTypeMapper.updateDynamic(complaintType);
	}
}
