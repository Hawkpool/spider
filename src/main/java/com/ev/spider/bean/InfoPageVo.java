package com.ev.spider.bean;

import com.xuxueli.crawler.annotation.PageFieldSelect;
import com.xuxueli.crawler.annotation.PageSelect;
import lombok.Data;

import java.util.List;

import static com.xuxueli.crawler.conf.XxlCrawlerConf.SelectType.ATTR;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-07 15:21
 */
// PageSelect 注解：从页面中抽取出一个或多个VO对象；
@PageSelect(cssQuery = "body")
@Data
public class InfoPageVo {
    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > span")
    private String replaceString;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(3) > td:nth-child(2) > span")
    private String replaceString1;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > span > a")
    private String replaceVersion;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > span > a:nth-child(2)")
    private String replaceVersion1;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(1) > td > h3")
    private String name;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > font > strong")
    private String nowVersion;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(2) > a > font > strong")
    private String status;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > span > a", selectType = ATTR,  selectVal = "href")
    private String  thirdPath;

    @PageFieldSelect(cssQuery = "body > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(8) > td > table:nth-child(1) > tbody > tr > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > span > a:nth-child(2)", selectType = ATTR,  selectVal = "href")
    private String  thirdPath1;
}
