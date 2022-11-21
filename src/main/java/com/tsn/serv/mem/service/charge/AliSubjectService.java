package com.tsn.serv.mem.service.charge;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.charge.AliSubject;
import com.tsn.serv.mem.mapper.charge.AliSubjectMapper;

@Service
public class AliSubjectService {
	
	@Autowired
	private AliSubjectMapper aliSubjectMapper;

	public void getAliSubjectList(Page page) {
		List<AliSubject> aliSubjectList = aliSubjectMapper.getAliSubjectList(page);
		page.setData(aliSubjectList);
	}

	public void addAliSubject(List<AliSubject> aliSubjectList) {
		aliSubjectList.stream().forEach(ali -> {
			aliSubjectMapper.insertDynamic(ali);
		});
	}

	public void deleteAliSubject(List<AliSubject> aliSubjectList) {
		List<Integer> idList = new ArrayList<>();
		aliSubjectList.stream().forEach(ali -> {
			idList.add(ali.getId());
		});
		aliSubjectMapper.deleteAliSubjectByIds(idList);
	}
}
