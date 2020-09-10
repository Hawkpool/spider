package com.ev.spider.bean;

import com.xuxueli.crawler.annotation.PageFieldSelect;
import com.xuxueli.crawler.annotation.PageSelect;
import lombok.Data;


import static com.xuxueli.crawler.conf.XxlCrawlerConf.SelectType.ATTR;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-07 15:21
 */
// PageSelect 注解：从页面中抽取出一个或多个VO对象；
@PageSelect(cssQuery = "body")
@Data
public class ListPageVo {
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > thead > tr:nth-child(2) > td:nth-child(1) > a > font")
    private String version;
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > thead > tr:nth-child(2) > td:nth-child(2) > font")
    private String name;
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > thead > tr:nth-child(2) > td:nth-child(5) > font")
    private String status;
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > thead > tr:nth-child(2) > td:nth-child(1) > a", selectType = ATTR,  selectVal = "href")
    private String secondPath;
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > thead > tr:nth-child(2)", selectType = ATTR,  selectVal = "title")
    private  String title;
}
