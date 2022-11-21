package com.tsn.serv.pub.mapper.complaint;


import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.pub.entity.complaint.Complaint;

public interface ComplaintMapper {

    int delete(Integer id);

    int insert(Complaint complaint);

    int insertDynamic(Complaint complaint);

    int updateDynamic(Complaint complaint);

    int update(Complaint complaint);

    Complaint selectById(Integer id);

    List<Map<String, Object>> getComplaintPage(Page page);
}