package com.tsn.serv.pub.mapper.complaint;


import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.pub.entity.complaint.ComplaintType;

public interface ComplaintTypeMapper {

    int delete(Integer complaintTypeId);

    int insert(ComplaintType complaintType);

    int insertDynamic(ComplaintType complaintType);

    int updateDynamic(ComplaintType complaintType);

    int update(ComplaintType complaintType);

    ComplaintType selectByComplaintTypeId(Integer complaintTypeId);

    List<ComplaintType> selectAllComplaintType();

    List<ComplaintType> getComplaintTypePage(Page page);
}