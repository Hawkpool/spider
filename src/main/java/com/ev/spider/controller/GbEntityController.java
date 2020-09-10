package com.ev.spider.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import com.ev.spider.SpiderApplication;
import com.ev.spider.bean.GbEntity;
import com.ev.spider.service.GbEntityService;
import com.ev.spider.utils.ExcelListener;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 16:07
 */
@Controller
@RequestMapping("/GbEntity")
public class GbEntityController {
    @Autowired
    GbEntityService gbEntityService;

    @GetMapping("/singleSpider")
    @ResponseBody
    public String singleSpider(String  searchVal){
        JSONObject jsonObject = new JSONObject();
        gbEntityService.singleSpider( searchVal,  jsonObject);
        return jsonObject.toString();
    }

    @RequestMapping("/startSpider")
    @ResponseBody
    public String startSpider(){
        try {
            return gbEntityService.startSpider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "filed";
    }


    @RequestMapping("/getAll")
    @ResponseBody
    public String getAll(){
        JSONObject jsonObject = new JSONObject();
        List<GbEntity> list = gbEntityService.getAllGbEntity();
        JSONArray jsonArray = JSONArray.fromObject(list);
        try {
            jsonObject.put("code",0);
            jsonObject.put("data",jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @RequestMapping("/removeAll")
    @ResponseBody
    public void removeAll(){
        gbEntityService.removeAllGbEntity();
        ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(() -> {
            SpiderApplication.context.close();
            SpiderApplication.context = SpringApplication.run(SpiderApplication.class,
                    SpiderApplication.args);
        });
        threadPool.shutdown();
    }

    @RequestMapping("/import")
    @ResponseBody
    public String importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader excelReader = null;
        //实例化实现了AnalysisEventListener接口的类
        ExcelListener listener = new ExcelListener();

        String fileName = file.getOriginalFilename();
        String fileType=fileName.substring(fileName.lastIndexOf(".")+1);

        if("xls".equalsIgnoreCase(fileType)){
            //传入参数
            excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS, null, listener);
        }else{
            excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLSX, null, listener);
        }


        //读取信息
        excelReader.read(new Sheet(1, 1, GbEntity.class));

        //获取数据
        List<Object> list = listener.getDatas();

        List<GbEntity> catagoryList = new ArrayList<GbEntity>();
        GbEntity gbEntity = new GbEntity();

        //转换数据类型,并插入到数据库
        for (int i = 0; i < list.size(); i++) {
            gbEntity = (GbEntity) list.get(i);
            gbEntityService.saveGbEntity(gbEntity);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("answer","success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    /**
     * 文件下载（失败了会返回一个有部分数据的Excel
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        List<GbEntity> list = gbEntityService.getAllGbEntity();
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), GbEntity.class).sheet("模板").doWrite(list);
    }

}
