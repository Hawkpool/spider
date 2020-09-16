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
@PageSelect(cssQuery = "#body")
@Data
public class FundInfoPageVo {
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-header > div.fundDetail-tit > div > span.ui-num")
    private String unid;
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-header > div.fundDetail-tit > div")
    private String name;
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(1) > td:nth-child(1) > a")
    private String fundType;
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(1) > td:nth-child(1)")
    private String fundRisk;//截取 split("|")[1]
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(1) > td:nth-child(2)")
    private String fundScope;//截取 split("（")[0]
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(1) > td:nth-child(3) > a")
    private String fundManager;//基金经理
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(2) > td:nth-child(3) > div", selectType = ATTR,  selectVal = "class")
    private String fundRank;//截取 jjpj4 中的数字
    @PageFieldSelect(cssQuery = "#body > div:nth-child(12) > div > div > div.fundDetail-main > div.fundInfoItem > div.infoOfFund > table > tbody > tr:nth-child(2) > td:nth-child(1)")
    private String startDate;//截取 split("：")[1]

}
