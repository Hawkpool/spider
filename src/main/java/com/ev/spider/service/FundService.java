package com.ev.spider.service;

import com.ev.spider.bean.fund.FundBaseEntity;

import java.util.List;

public interface FundService {
    public void startSpider() throws Exception;
    public void saveEntity(FundBaseEntity fundBaseEntity);
    public void removeAllEntity() throws Exception;
    public List<FundBaseEntity> getAllEntity();
}
