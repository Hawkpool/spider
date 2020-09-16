package com.ev.spider.service;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.ev.spider.bean.ProxyIpEntity;
import com.ev.spider.bean.fund.FundBaseEntity;
import com.ev.spider.bean.fund.FundInfoPageVo;
import com.ev.spider.bean.fund.FundListPageVo;
import com.ev.spider.dao.FundBaseEntityRepository;
import com.ev.spider.utils.HttpUtil;
import com.ev.spider.utils.ProxyUtil;
import com.ev.spider.utils.TimmerUtil;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.loader.strategy.HtmlUnitPageLoader;
import com.xuxueli.crawler.parser.PageParser;
import com.xuxueli.crawler.proxy.strategy.RandomProxyMaker;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class FundServiceImpl implements FundService{
    @Autowired
    private FundBaseEntityRepository fundBaseEntityRepository;

    RandomProxyMaker proxyMaker = new RandomProxyMaker();
    Map<String,String> cookieMap = new HashMap<>();
    Map<String,String> headerMap = new HashMap<>();
    HtmlUnitPageLoader htmlUnitPageLoader = new HtmlUnitPageLoader();

    @Override
    public void startSpider() throws Exception{
        //优先爬取代理ip,配置ip代理池
        proxyMaker.clear();
        ProxyUtil.countDownUpdateProxy(120,proxyMaker);//定时更新代理池
        ProxyUtil.getProxyList(proxyMaker);
//        cookieMap.put("st_inirUrl","http%3A%2F%2Ffund.eastmoney.com%2Ffund.html");
//        cookieMap.put("st_asi","delete");
//        cookieMap.put("st_sp","2020-09-12%2006%3A52%3A40");
//        cookieMap.put("st_inirUrl","http%3A%2F%2Ffund.eastmoney.com%2Ffund.html");
        headerMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headerMap.put("Accept-Encoding","gzip, deflate");
        headerMap.put("Accept-Language","zh,en-US;q=0.9,en;q=0.8");
        headerMap.put("Cache-Control","max-age=0");
        headerMap.put("Connection","keep-alive");
        headerMap.put("Host","fund.eastmoney.com");
        headerMap.put("Referer","http://fund.eastmoney.com/fund.html");
        headerMap.put("Upgrade-Insecure-Requests","1");
        List<FundBaseEntity> list = fundBaseEntityRepository.findAll();

        Thread.sleep(5000);
//        firstSpider();
        secondSpider(list.get(0).getUnid());
//        for(int i=0;i<list.size();i++){
//            String unid = list.get(i).getUnid();
//
//            Thread.sleep(1000);
//        }
        Thread.sleep(60);
        boom();
    }
    //用于开启嵌套循环-不建议开启,容易被封ip
    public void boom() throws Exception{
        startSpider();
    }
    @Override
    public void saveEntity(FundBaseEntity fundBaseEntity){
        fundBaseEntityRepository.save(fundBaseEntity);
    }

    @Override
    public void removeAllEntity() throws Exception {
        fundBaseEntityRepository.deleteAll();
    }

    @Override
    public List<FundBaseEntity> getAllEntity() {
        return fundBaseEntityRepository.findAll();
    }

    public void firstSpider(){
        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://fund.eastmoney.com/fund.html#os_0;isall_0;ft_;pt_1")
                .setHeaderMap(headerMap)
                .setWhiteUrlRegexs("http://fund.eastmoney.com/+.*")
//                .setCookieMap(cookieMap)
                .setAllowSpread(true)
                .setThreadCount(8)
                .setTimeoutMillis(6000)
                .setPauseMillis(5000)
                .setProxyMaker(proxyMaker)
                .setFailRetryCount(10)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36 Edg/85.0.564.51")
//                .setPageLoader(htmlUnitPageLoader)
                .setPageParser(new PageParser<FundListPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, FundListPageVo pageVo) {
                        System.out.println(proxyMaker.toString());
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        if(!StringUtils.isEmpty(pageVo.getUnid())&&!StringUtils.isEmpty(pageVo.getName())){
                            FundBaseEntity fbe = new FundBaseEntity();
                            fbe.setUnid(pageVo.getUnid());
                            fbe.setName(pageVo.getName());
                            fundBaseEntityRepository.save(fbe);
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(120,crawler);//定时自杀程序,避免内存爆掉
    }

    public void secondSpider(String unid){
        XxlCrawler crawler = new XxlCrawler.Builder()
        .setUrls("http://fund.eastmoney.com/"+unid+".html?spm=search")
//        .setUrls("http://fund.eastmoney.com/fund.html#os_0;isall_0;ft_;pt_1")
        .setHeaderMap(headerMap)
        .setWhiteUrlRegexs("http://fund.eastmoney.com/+.*")
        // .setCookieMap(cookieMap)
        .setAllowSpread(true)
        .setThreadCount(8)
        .setTimeoutMillis(6000)
        .setPauseMillis(5000)
        .setProxyMaker(proxyMaker)
        .setFailRetryCount(10)
        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36 Edg/85.0.564.51")
    //                .setPageLoader(htmlUnitPageLoader)
        .setPageParser(new PageParser<FundInfoPageVo>() {
            @Override
            public void parse(Document html, Element pageVoElement, FundInfoPageVo pageVo) {
                // 解析封装 PageVo 对象
                String pageUrl = html.baseUri();
                System.out.println(pageUrl + "：" + pageVo.toString());
//                if(!StringUtils.isEmpty(pageVo.getUnid())&&unid.equals(pageVo.getUnid())){
                    if(!StringUtils.isEmpty(pageVo.getUnid())){
                        FundBaseEntity fbe = fundBaseEntityRepository.findByUnid(pageVo.getUnid());
                        if(fbe==null){
                            fbe = new FundBaseEntity();
                        }
                        if(!StringUtils.isEmpty(pageVo.getName())){
                            if(pageVo.getName().contains("(")){
                                fbe.setName( pageVo.getName().trim().split("\\(")[0]);
                            }
                        }
                        fbe.setUnid(pageVo.getUnid());
                        fbe.setFundType(StringUtils.isEmpty(pageVo.getFundType())?"":pageVo.getFundType());
                        fbe.setFundManager(StringUtils.isEmpty(pageVo.getFundManager())?"":pageVo.getFundManager());
                        if(!StringUtils.isEmpty(pageVo.getFundRisk())){
                            if(pageVo.getFundRisk().contains("|")){
                                fbe.setFundRisk( pageVo.getFundRisk().trim().split("\\|")[1]);
                            }
                        }
                        if(!StringUtils.isEmpty(pageVo.getFundScope())){
                            fbe.setFundScope(pageVo.getFundScope().trim().substring(pageVo.getFundScope().lastIndexOf("：")+1,pageVo.getFundScope().lastIndexOf("（")));
                        }
                        if(!StringUtils.isEmpty(pageVo.getFundRank())){
                            fbe.setFundRank(pageVo.getFundRank().trim().substring(4));
                        }
                        if(!StringUtils.isEmpty(pageVo.getStartDate())){
                            fbe.setStartDate(pageVo.getStartDate().trim().split("：")[1]);
                        }

                        fundBaseEntityRepository.save(fbe);
                    }

                }
//            }
        }).build();
        crawler.start(true);
        TimmerUtil.countDown(60,crawler);//定时自杀程序,避免内存爆掉
    }
}
