package com.tsn.serv.pub.mapper.feedback;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.pub.entity.feedback.Feedback;

public interface FeedbackMapper {

    int delete(Integer id);

    int insert(Feedback feedback);

    int insertDynamic(Feedback feedback);

    int updateDynamic(Feedback feedback);

    int update(Feedback feedback);

    Feedback selectById(Integer id);

    List<Feedback> getFeedbackPage(Page page);
}