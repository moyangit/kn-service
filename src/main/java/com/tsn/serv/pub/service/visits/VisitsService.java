package com.tsn.serv.pub.service.visits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.time.DateUtils;

@Service
public class VisitsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisHandler redisHandler;

    public void upVisits(String source) {
        if (!StringUtils.isEmpty(source)) {
            String date = DateUtils.getCurrYMD("yyyyMMdd");
            String key = "visits:" + source + ":" + date;
            try {
                redisHandler.incr(key, 1);
            } finally {
                // 两个月 2 * 30 * 24 * 60 * 60
                redisHandler.expire(key, 2 * 30 * 24 * 60 * 60);
            }
        }
    }

    public Map<Object, Object> getVisitsChart(String time, String source) {
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date monthDate = new Date(Long.parseLong(time));
        calendar.setTime(monthDate);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] keys = new String[actualMaximum];
        for (int i =0; i < actualMaximum; i++){
            String yyyyMM = new SimpleDateFormat("yyyyMM").format(monthDate);
            String key;
            if (i < 9) {
                key= "visits:" + source + ":" + yyyyMM + "0" + (i + 1);
            } else {
                key = "visits:" + source + ":" + yyyyMM + (i + 1);
            }
            keys[i] = key;
            keyList.add(String.valueOf(i + 1) + "号");
        }
        List<Object> valueList = redisHandler.mGet(keys);
        for (int i = 0; i < valueList.size(); i++){
            if (valueList.get(i) == null){
                valueList.set(i, 0);
            }
        }

        Map<Object, Object> visitsMap = new HashMap<>();
        visitsMap.put("keyList", keyList);
        visitsMap.put("valueList", valueList);
         return visitsMap;
    }

    public List<Map<Object, Object>> getSourceType() {
        List<Map<Object, Object>> sourceTypeList = new ArrayList<>();
        Set<String> keys = redisHandler.keys("visits:*");
        Map<String, Object> keyMap = new HashMap<>();
        for (String key : keys) {
            String[] split = key.split(":");
            keyMap.put(split[1], split[1]);
        }

        for (String key : keyMap.keySet()) {
            Map<Object, Object> rtnMap = new HashMap<>();
            rtnMap.put("key", key);
            rtnMap.put("display_name", key);
            sourceTypeList.add(rtnMap);
        }
        return sourceTypeList;
    }
}
