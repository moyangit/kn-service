package com.tsn.serv.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Transformation {

    public Map<String,Object> ObjectToMap(Object o){
        Gson gson = new Gson();
        Map<String, Object> toMmap = new HashMap<>();
        toMmap = gson.fromJson(o.toString(), toMmap.getClass());
        return toMmap;
    }

    public Date ObjectToDate(Object o){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        try {
            if (!o.toString().isEmpty()){
                Date date = simpleDateFormat.parse(o.toString());
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
