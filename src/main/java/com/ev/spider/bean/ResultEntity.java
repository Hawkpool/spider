package com.ev.spider.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 16:13
 */
public class ResultEntity {
    private Map<String,Object> map = new HashMap<String,Object>();

    public static ResultEntity success(){
        //相当于调用下面的map 然后把值存map里面 。
        ResultEntity entity = new ResultEntity();
        entity.getMap().put("statusCode",200);
        entity.getMap().put("message","发送成功");
        return entity;
    }
    //用来存值，对象，集合
    public  ResultEntity put (String key,Object value){
        this.getMap().put(key,value);
        return  this;
    }
    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

}
