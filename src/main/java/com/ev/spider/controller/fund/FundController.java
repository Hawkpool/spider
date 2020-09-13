package com.ev.spider.controller.fund;

import com.ev.spider.SpiderApplication;
import com.ev.spider.bean.fund.FundBaseEntity;
import com.ev.spider.service.FundService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/fund")
public class FundController {

    @Autowired
    FundService fundService;


    @RequestMapping("/getAll")
    @ResponseBody
    public String getAll(){
        JSONObject jsonObject = new JSONObject();
        List<FundBaseEntity> list = fundService.getAllEntity();
        JSONArray jsonArray = JSONArray.fromObject(list);
        try {
            jsonObject.put("code",0);
            jsonObject.put("data",jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @RequestMapping("/removeAll")
    @ResponseBody
    public void removeAll(){
        try {
            fundService.removeAllEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/startSpider")
    @ResponseBody
    public String startSpider(){
        try {
            fundService.startSpider();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "filed";
    }
}
