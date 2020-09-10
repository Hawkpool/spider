package com.ev.spider.service;

import com.ev.spider.bean.GbEntity;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 17:21
 */
public interface GbEntityService {
    public List<GbEntity> getAllGbEntity();
    public void singleSpider(String searchVal, JSONObject jsonObject);
    public GbEntity saveGbEntity(GbEntity gbEntity);
    public long countAll();
    public String startSpider() throws Exception;
    public void removeAllGbEntity();
}
