package com.ev.spider.bean.fund;

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
@PageSelect(cssQuery = "#oTable > tbody > tr")
@Data
public class FundListPageVo {
    @PageFieldSelect(cssQuery = "td.bzdm")
    private String unid;
    @PageFieldSelect(cssQuery = "td.tol > nobr > a:nth-child(1)")
    private String name;
}
