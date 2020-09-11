package com.ev.spider.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-11 9:56
 */
@Slf4j
public class HttpUtil {

    /**
     * 封装httpRequest Get方法
     *
     * @return JsonObject
     */
    public static String httpRequestGet(String url, Map<String, Object> paramsMap, Integer connectionTimeout) {
        try {
            HttpResponse response = HttpRequest.get(url)
                    .form(paramsMap)
                    .timeout(connectionTimeout)
                    .execute();
            if(response.getStatus() == 200) {
                String result = response.body();
                //JSONObject jsonResult = JSON.parseObject(result);
                return result;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String errTime = dateFormat.format(new Date());
                log.error(errTime + "  response error(" + response.getStatus() + "):" + url);
                return null;
            }

        } catch (Exception ex) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String errTime = dateFormat.format(new Date());
            log.error(errTime + "  request shell CRM error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * 封装httpRequest Post方法
     *
     * @return JsonObject
     */
    public JSONObject httpRequestPost (String url, String jsonBody,  Integer connectionTimeout) {
        try {
            // 向壳牌CRM发起请求，使用cn.hutool.http.HttpRequest，返回对象为cn.hutool.http.HttpResponse
            HttpResponse response = HttpRequest.post(url)
                    .contentType("application/json")
                    .timeout(connectionTimeout)
                    .body(jsonBody)
                    .execute();

            // 判断请求状态是否正常
            if(response.getStatus() == 200) {
                String result = response.body();
                com.alibaba.fastjson.JSONObject jsonResult = JSON.parseObject(result);
                return jsonResult;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String errTime = dateFormat.format(new Date());
                log.error(errTime + "  response error(" + response.getStatus() + "):" + url);
                return null;
            }
        } catch (Exception ex){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String errTime = dateFormat.format(new Date());
            log.error(errTime + "  request shell CRM error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * 封装httpRequest Put方法
     *
     * @return JsonObject
     */
    public JSONObject httpRequestPut (String url, String jsonBody,  Integer connectionTimeout) {
        try {
            // 向壳牌CRM发起请求，使用cn.hutool.http.HttpRequest，返回对象为cn.hutool.http.HttpResponse
            HttpResponse response = HttpRequest.put(url)
                    .contentType("application/json")
                    .timeout(connectionTimeout)
                    .body(jsonBody)
                    .execute();

            // 判断请求状态是否正常
            if(response.getStatus() == 200) {
                String result = response.body();
                com.alibaba.fastjson.JSONObject jsonResult = JSON.parseObject(result);
                return jsonResult;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String errTime = dateFormat.format(new Date());
                log.error(errTime + "  response error(" + response.getStatus() + "):" + url);
                return null;
            }
        } catch (Exception ex){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String errTime = dateFormat.format(new Date());
            log.error(errTime + "  request shell CRM error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * 自定义httpPost方法，使用org.apache.http.client实现
     * 问题：请求超时，无法通过VPN请求接口，需进一步调试
     *
     * @param url
     * @param object
     * @return JsonObject
     */
    public static Object httpClientPost(String url, Object object, Integer connectionTimeout, Integer connectionRequestTimeout, Integer socketTimeout) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求的Config
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout).build();
        httpPost.setConfig(requestConfig);

        // 设置请求的header
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");

        // 设置请求的body
        // JSONObject jsonParam = JSONObject.parseObject(JSONObject.toJSONString(registerMemberParam));
        String jsonString = JSONObject.toJSONString(object);
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        // StringEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        httpPost.setEntity(entity);

        // 执行请求
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            System.out.println("得到的结果:" + response.getStatusLine());//得到请求结果
            HttpEntity entityResult = response.getEntity();//得到请求回来的数据

            // 解析返回结果
            String result = EntityUtils.toString(entityResult, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(result);

            // 打印执行结果
            System.out.println(jsonObject);
            return jsonObject;
        } catch (Exception ex) {
            System.out.println("发送 POST 请求出现异常！" + ex);
            ex.printStackTrace();
            return null;
        }
    }
}

