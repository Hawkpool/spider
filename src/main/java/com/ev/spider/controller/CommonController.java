package com.ev.spider.controller;

import com.alibaba.excel.util.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 17:47
 */
@Controller
public class CommonController {
    @RequestMapping("/index")
    public String toIndexAction(Model model, String refresh){
        if(StringUtils.isEmpty(refresh)){
            refresh = "自动刷新";
        }
        model.addAttribute("refresh",refresh);
        return "index";
    }
    @RequestMapping("/fund")
    public String toFundAction(Model model, String refresh){
        model.addAttribute("refresh",refresh);
        return "fund";
    }
}
