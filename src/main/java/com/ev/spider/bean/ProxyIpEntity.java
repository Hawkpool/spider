package com.ev.spider.bean;

import lombok.Data;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-11 9:09
 */
@Data
public class ProxyIpEntity {
    private String unique_id;
    private String ip;
    private String port;
    private String country;
    private String ip_address;
    private String anonymity;
    private String protocol;
    private String isp;
    private String speed;
    private String validated_at;
    private String created_at;
    private String updated_at;
}
