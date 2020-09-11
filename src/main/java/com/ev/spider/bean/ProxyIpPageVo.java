package com.ev.spider.bean;

import com.xuxueli.crawler.annotation.PageFieldSelect;
import com.xuxueli.crawler.annotation.PageSelect;
import lombok.Data;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-11 8:46
 */
@PageSelect(cssQuery = "body")
@Data
public class ProxyIpPageVo {
    @PageFieldSelect(cssQuery = "body > pre")
    private String body;
}
