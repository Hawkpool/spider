package com.ev.spider.service;

import com.alibaba.excel.util.StringUtils;
import com.ev.spider.bean.*;
import com.ev.spider.dao.GbEntityRepository;
import com.ev.spider.utils.ProxyUtil;
import com.ev.spider.utils.TimmerUtil;
import com.ev.spider.utils.UrlUtil;
import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.loader.strategy.HtmlUnitPageLoader;
import com.xuxueli.crawler.parser.PageParser;
import com.xuxueli.crawler.proxy.strategy.RandomProxyMaker;
import com.xuxueli.crawler.proxy.strategy.RoundProxyMaker;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 17:22
 */
@Service
public class GbEntityServiceImpl implements GbEntityService{
    @Autowired
    private GbEntityRepository gbEntityRepository;
    RandomProxyMaker proxyMaker = new RandomProxyMaker();
    Map<String,String> cookieMap = new HashMap<>();
    Map<String,String> headerMap = new HashMap<>();
    Integer retryCount = 5;
    Integer threadCount = 3;
    Integer timeoutMillis = 6000;
    Integer pauseMillis = 5000;
    HtmlUnitPageLoader htmlUnitPageLoader = new HtmlUnitPageLoader();

    public List<GbEntity> getAllGbEntity(){
        List<GbEntity> list = gbEntityRepository.findAll();
        return list;
    }

    public void singleSpider(String searchVal, JSONObject jsonObject){

        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://www.csres.com/s.jsp?keyword="+ UrlUtil.getURLEncoderString(searchVal)+"&submit12=%B1%EA%D7%BC%CB%D1%CB%F7&xx=on&wss=on&zf=on&fz=on&pageSize=25&pageNum=1")
                .setProxyMaker(proxyMaker)
                .setAllowSpread(false)
                .setThreadCount(1)
                .setPauseMillis(6000)
                .setPauseMillis(5000)
                .setPageParser(new PageParser<ListPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, ListPageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        if(!StringUtils.isEmpty(pageVo.getTitle())){
                            jsonObject.put("data",pageVo.getTitle());
                        }else{
                            jsonObject.put("data","未知");
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(10,crawler);//定时自杀程序,避免内存爆掉
    }

    public void removeAllGbEntity(){
        gbEntityRepository.deleteAll();
    }

    public GbEntity saveGbEntity(GbEntity gbEntity){
        return gbEntityRepository.save(gbEntity);
    }

    public long countAll(){
        return gbEntityRepository.count();
    }

    public String startSpider() throws Exception{
        //优先爬取代理ip,配置ip代理池
        proxyMaker.clear();
        ProxyUtil.countDownUpdateProxy(120,proxyMaker);//定时更新代理池
        ProxyUtil.getProxyList(proxyMaker);
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        cookieMap.put("cCount"+formatter.format(date),"1");
        headerMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headerMap.put("Accept-Encoding","gzip, deflate");
        headerMap.put("Accept-Language","zh,en-US;q=0.9,en;q=0.8");
        headerMap.put("Cache-Control","max-age=0");
        headerMap.put("Connection","keep-alive");
        headerMap.put("Upgrade-Insecure-Requests","1");
        Thread.sleep(5000);

        //获取所有实体,并拆分4线程,基本保证每次点击按钮每条数据可查3次
        List<GbEntity> list = gbEntityRepository.findAll();
        List<GbEntity> list1 = gbEntityRepository.findAll();
        List<GbEntity> list2 = new ArrayList<>();
        List<GbEntity> list3 = new ArrayList<>();
        for(int i=list.size();i>list.size()/2;i--){
            list2.add(list.get(i-1));
        }
        for(int i=list1.size();i>list1.size()/2;i--){
            list3.add(list1.get(i-1));
        }


        Collections.reverse(list1);
        Collections.reverse(list2);
        Collections.reverse(list3);

        Thread t1 = new MyThread(list);
        t1.start();

        Thread.sleep(5000);

        Thread t2 = new MyThread(list1);
        t2.start();

        Thread.sleep(10000);

        Thread t3 = new MyThread(list2);
        t3.start();

        Thread.sleep(1000);

        Thread t4 = new MyThread(list3);
        t4.start();


        Thread.sleep(600000);
        boom();
        return "success";
    }

    public void MySpider(List<GbEntity> list) throws Exception{
        for (GbEntity gbEntity : list) {
            String name = gbEntity.getName();
            String version = gbEntity.getVersion();
            String status = gbEntity.getStatus();
            String updatedName = gbEntity.getUpdatedName();
            String updatedVersion = gbEntity.getUpdatedVersion();
            String dataTypes = gbEntity.getDataType();
            String secondSpiderPath = gbEntity.getSecondSpiderPath();
            String thirdSpiderPath = gbEntity.getThirdSpiderPath();

            if(!StringUtils.isEmpty(updatedName)&&!StringUtils.isEmpty(updatedVersion)&&"999".equals(dataTypes)&&"现行".equals(status)){
//                if(updatedName!=null && updatedVersion!=null){
                    continue;
//                }
            }

            if(!"999".equals(dataTypes)&&!StringUtils.isEmpty(thirdSpiderPath)){
                System.out.println("五次查询");
                thirdSpider( thirdSpiderPath, gbEntity);
            }

            if(StringUtils.isEmpty(thirdSpiderPath)&&"777".equals(dataTypes)&&!StringUtils.isEmpty(updatedVersion)){
                System.out.println("四次查询");
                fourthSpider(updatedVersion,gbEntity);
            }

            if(!StringUtils.isEmpty(secondSpiderPath)&&StringUtils.isEmpty(updatedVersion)){//查询url
                System.out.println("三次查询");
                thirdSpider( secondSpiderPath, gbEntity);
            }

            if("888".equals(dataTypes)&&StringUtils.isEmpty(secondSpiderPath)){ //属于待查名字的数据-大概率查不到
                System.out.println("二次查询");
//                firstSpider(version,gbEntity);
                secondSpider(name,gbEntity);
            }

            if(!StringUtils.isEmpty(version)&&StringUtils.isEmpty(dataTypes)){//待查版本编号的数据,通常属于第一次查询
                System.out.println("初次查询");
                firstSpider(version,gbEntity);
            }

        }
    }
    /**
     * 自定义线程
     */
    class MyThread extends Thread{
        /*线程名称*/
        private List<GbEntity> list;

        public MyThread( List<GbEntity> list) {
            this.list = list;
        }
        @Override
        public void run() {
            try {
                MySpider( list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void firstSpider(String version, GbEntity gbEntity){
        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://www.csres.com/s.jsp?keyword="+ UrlUtil.getURLEncoderString(version)+"&xx=on&wss=on&zf=on&fz=on&pageSize=25&pageNum=1&SortIndex=1&WayIndex=0&nowUrl=")
                .setProxyMaker(proxyMaker)
                .setCookieMap(cookieMap)
                .setAllowSpread(false)
                .setThreadCount(threadCount)
                .setFailRetryCount(retryCount)
                .setTimeoutMillis(timeoutMillis)
                .setPauseMillis(pauseMillis)
//                .setPageLoader(htmlUnitPageLoader)
                .setPageParser(new PageParser<ListPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, ListPageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        if("现行".equals(pageVo.getStatus())){
                            gbEntity.setUpdatedName(pageVo.getName());
                            gbEntity.setStatus("现行");
                            gbEntity.setUpdatedVersion(pageVo.getVersion());
//                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setDataType("999");
                        }
                        if("作废".equals(pageVo.getStatus())){
                            gbEntity.setStatus("作废");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setDataType("777");
                        }
                        if("废止".equals(pageVo.getStatus())){
                            gbEntity.setStatus("废止");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setDataType("777");
                        }
                        if(StringUtils.isEmpty(pageVo.getName())&&StringUtils.isEmpty(pageVo.getVersion())&&StringUtils.isEmpty(pageVo.getStatus())&&StringUtils.isEmpty(pageVo.getSecondPath())){
                            gbEntity.setDataType("888");//待查名字
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(240,crawler);//定时自杀程序,避免内存爆掉
        gbEntityRepository.save(gbEntity);//数据回存
    }

    public void secondSpider(String name, GbEntity gbEntity){
        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://www.csres.com/s.jsp?keyword="+ UrlUtil.getURLEncoderString(name)+"&submit12=%B1%EA%D7%BC%CB%D1%CB%F7&xx=on&pageSize=25&pageNum=1")
                .setProxyMaker(proxyMaker)
                .setCookieMap(cookieMap)
                .setAllowSpread(false)
                .setThreadCount(threadCount)
                .setFailRetryCount(retryCount)
                .setTimeoutMillis(timeoutMillis)
                .setPauseMillis(pauseMillis)
//                .setPageLoader(htmlUnitPageLoader)
                .setPageParser(new PageParser<ListPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, ListPageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        if("现行".equals(pageVo.getStatus())){
                            gbEntity.setUpdatedName(pageVo.getName());
                            gbEntity.setUpdatedVersion(pageVo.getVersion());
//                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setStatus("现行");
                            gbEntity.setDataType("999");
                        }
                        if("作废".equals(pageVo.getStatus())){
                            gbEntity.setStatus("作废");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setDataType("777");
                        }
                        if("废止".equals(pageVo.getStatus())){
                            gbEntity.setStatus("废止");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                            gbEntity.setDataType("777");
                        }
                        if(StringUtils.isEmpty(pageVo.getName())&&StringUtils.isEmpty(pageVo.getVersion())&&StringUtils.isEmpty(pageVo.getStatus())&&StringUtils.isEmpty(pageVo.getSecondPath())){
                            gbEntity.setDataType("");//名字再查不出,回去再查version,避免因反爬虫导致version查不到
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(240,crawler);//定时自杀程序,避免内存爆掉
        gbEntityRepository.save(gbEntity);//数据回存
    }

    public void thirdSpider(String secondSpiderPath, GbEntity gbEntity){

        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://www.csres.com"+secondSpiderPath)
                .setProxyMaker(proxyMaker)
                .setCookieMap(cookieMap)
                .setAllowSpread(false)
                .setThreadCount(threadCount)
                .setFailRetryCount(retryCount)
                .setTimeoutMillis(timeoutMillis)
                .setPauseMillis(pauseMillis)
//                .setPageLoader(htmlUnitPageLoader)
                .setPageParser(new PageParser<InfoPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, InfoPageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        String replaceString  = pageVo.getReplaceString();
                        String replaceString1  = pageVo.getReplaceString1();
                        String replaceVersion = pageVo.getReplaceVersion();
                        String replaceVersion1 = pageVo.getReplaceVersion1();
                        String thirdPath = pageVo.getThirdPath();
                        String thirdPath1 = pageVo.getThirdPath1();
                        String status = pageVo.getStatus();
                        String name = pageVo.getName();
                        String nowVersion = pageVo.getNowVersion();
                        String updatedVersion = "";

                        if("现行".equals(status)){
                            if(nowVersion.equals(gbEntity.getVersion())){
                                gbEntity.setStatus("现行");
                            }
                            gbEntity.setUpdatedName(name);
                            gbEntity.setUpdatedVersion(nowVersion);
                            gbEntity.setDataType("999");
                        }else{
//                        if(!StringUtils.isEmpty(thirdPath)||!StringUtils.isEmpty(thirdPath1)){
//                            if(!StringUtils.isEmpty(thirdPath1)){
//                                if(replaceString1.contains("被")||replaceString.contains("被")){
//                                    gbEntity.setThirdSpiderPath(thirdPath1);
//                                    if(!StringUtils.isEmpty(replaceVersion1)){
//                                        gbEntity.setUpdatedVersion(replaceVersion1);
//                                    }else{
//                                        setUpdatedVersion( replaceString, replaceString1,  updatedVersion, gbEntity);
//                                    }
//                                }
//                            }else{
//                                if(replaceString1.contains("被")||replaceString.contains("被")){
//                                    gbEntity.setThirdSpiderPath(thirdPath);
//                                    if(!StringUtils.isEmpty(replaceVersion)){
//                                        gbEntity.setUpdatedVersion(replaceVersion);
//                                    }else{
//                                        setUpdatedVersion( replaceString, replaceString1,  updatedVersion, gbEntity);
//                                    }
//                                }
//                            }
//
//                            gbEntity.setDataType("777");//待二次查列表
//                        }
//else
                         if(!StringUtils.isEmpty(replaceString)||!StringUtils.isEmpty(replaceString1)){
                             //统一变量
                            if(StringUtils.isEmpty(replaceString)&&!StringUtils.isEmpty(replaceString1)){
                                replaceString = replaceString1;
                            }
                            if(replaceString.contains(";")||replaceString.contains("被")){
                                String[] array = replaceString.split(";");
                                for (String s : array) {
                                    if(s.contains("被")){
                                        if(s.contains("替代")){
                                            updatedVersion = s.substring(s.lastIndexOf("被")+1,s.lastIndexOf("替代"));
                                        }
                                        if(s.contains("代替")){
                                            updatedVersion = s.substring(s.lastIndexOf("被")+1,s.lastIndexOf("代替"));
                                        }
                                        gbEntity.setUpdatedVersion(updatedVersion);
                                        gbEntity.setDataType("777");//待二次查列表
                                    }
                                }
                            }
                        }

//                        if(StringUtils.isEmpty(pageVo.getName())&&StringUtils.isEmpty(pageVo.getStatus())&&StringUtils.isEmpty(pageVo.getReplaceString())){
//                            gbEntity.setDataType("000");//详情页打不开
//                        }
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(240,crawler);//定时自杀程序,避免内存爆掉
        gbEntityRepository.save(gbEntity);//数据回存
    }

    public void fourthSpider(String version, GbEntity gbEntity){
        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls("http://www.csres.com/s.jsp?keyword="+ UrlUtil.getURLEncoderString(version)+"&submit12=%B1%EA%D7%BC%CB%D1%CB%F7&xx=on&wss=on&zf=on&fz=on&pageSize=25&pageNum=1")
                .setProxyMaker(proxyMaker)
                .setCookieMap(cookieMap)
                .setAllowSpread(false)
                .setThreadCount(threadCount)
                .setFailRetryCount(retryCount)
                .setTimeoutMillis(timeoutMillis)
                .setPauseMillis(pauseMillis)
//                .setPageLoader(htmlUnitPageLoader)
                .setPageParser(new PageParser<ListPageVo>() {
                    @Override
                    public void parse(Document html, Element pageVoElement, ListPageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = html.baseUri();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                        if("现行".equals(pageVo.getStatus())){
                            if(pageVo.getVersion().equals(gbEntity.getVersion())){
                                gbEntity.setStatus("现行");
                            }
                            gbEntity.setUpdatedName(pageVo.getName());
                            gbEntity.setUpdatedVersion(pageVo.getVersion());
                            gbEntity.setDataType("999");
                        }
                        if("作废".equals(pageVo.getStatus())){
                            gbEntity.setStatus("作废");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                        }
                        if("废止".equals(pageVo.getStatus())){
                            gbEntity.setStatus("废止");
                            gbEntity.setSecondSpiderPath(pageVo.getSecondPath());
                        }
                        if(StringUtils.isEmpty(pageVo.getName())&&StringUtils.isEmpty(pageVo.getVersion())&&StringUtils.isEmpty(pageVo.getStatus())&&StringUtils.isEmpty(pageVo.getSecondPath())){
                            gbEntity.setDataType("777");//待查名字
                        }
                    }
                }).build();
        crawler.start(true);
        TimmerUtil.countDown(240,crawler);//定时自杀程序,避免内存爆掉
        gbEntityRepository.save(gbEntity);//数据回存
    }

    //用于开启嵌套循环-不建议开启,容易被封ip
    public void boom() throws Exception{
        startSpider();
    }

    public void setUpdatedVersion(String replaceString,String replaceString1, String updatedVersion,GbEntity gbEntity){
        if(!StringUtils.isEmpty(replaceString)||!StringUtils.isEmpty(replaceString1)){
            if(StringUtils.isEmpty(replaceString)&&!StringUtils.isEmpty(replaceString1)){
                replaceString = replaceString1;
            }
            if(replaceString.contains(";")){
                String[] array = replaceString.split(";");
                for (String s : array) {
                    if(s.contains("被")){
                        if(s.contains("替代")){
                            updatedVersion = s.substring(s.lastIndexOf("被")+1,s.lastIndexOf("替代"));
                        }
                        if(s.contains("代替")){
                            updatedVersion = s.substring(s.lastIndexOf("被")+1,s.lastIndexOf("代替"));
                        }
                        gbEntity.setUpdatedVersion(updatedVersion);
                        gbEntity.setDataType("777");//待二次查列表
                    }
                }
            }
        }
    }

}


