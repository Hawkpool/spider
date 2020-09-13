package com.ev.spider.utils;

import com.xuxueli.crawler.XxlCrawler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimmerUtil {

    public static void countDown(int second, XxlCrawler crawler) {
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
                crawler.stop();//停止爬虫
                timer.cancel();
            }

        }, new Date(end));
    }
}
