package com.ev.spider.utils;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.ev.spider.bean.ProxyIpEntity;
import com.xuxueli.crawler.proxy.ProxyMaker;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@Slf4j
public class ProxyUtil{

    public static void countDownUpdateProxy(int second,ProxyMaker proxyMaker) {
        //开始时间
        long start = System.currentTimeMillis();
        //结束时间
        final long end = start + second * 1000;

        final Timer timer = new Timer();
        //延迟0毫秒（即立即执行）开始，每隔1000毫秒执行一次
        timer.schedule(new TimerTask() {
            public void run() {
//                Log.e("MainActivity","此处实现倒计时，指定时长内，每隔1秒执行一次该任务");
            }
        }, 0, 1000);
        //计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                proxyMaker.clear();
                getProxyList(proxyMaker);
                timer.cancel();
            }

        }, new Date(end));
    }

    public static void getProxyList(ProxyMaker proxyMaker){
        List<Proxy> pl = new ArrayList<>();

        for(int i=1;i<5;i++){
            getEnoughProxyByPage(i,pl);
        }

        proxyMaker.addProxyList(pl);
    }

    private static void getEnoughProxyByPage(int page,List<Proxy> pl){
        String ss = HttpUtil.httpRequestGet( "https://ip.jiangxianli.com/api/proxy_ips?country=%E4%B8%AD%E5%9B%BD&order_by=validated_at&order_rule=ASC&page="+page, null, 30000);
        //非空判断
        if(!StringUtils.isEmpty(ss)){
            //转json对象
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(ss);
            String msg = jsonObject.get("msg").toString();
            //消息状态判断
            if("成功".equals(msg)){
                //数据截取
                com.alibaba.fastjson.JSONObject obj = JSON.parseObject( jsonObject.get("data").toString());
                String dataList = obj.get("data").toString();
                //获取代理ip对象列表
                List<ProxyIpEntity> li = JSON.parseArray(dataList,ProxyIpEntity.class);
                //遍历生成 proxy列表
                for (ProxyIpEntity proxyIpEntity : li) {
                    String ip = proxyIpEntity.getIp();
                    String port = proxyIpEntity.getPort();
                    try {
                        InetSocketAddress socketAddress = new InetSocketAddress(ip,Integer.parseInt(port));
                        Proxy proxy=new Proxy(Proxy.Type.HTTP,socketAddress);
                        System.out.println(ip+":"+port);
                        pl.add(proxy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
