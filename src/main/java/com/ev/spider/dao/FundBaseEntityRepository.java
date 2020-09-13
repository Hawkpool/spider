package com.ev.spider.dao;

import com.ev.spider.bean.fund.FundBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 14:32
 */
public interface FundBaseEntityRepository extends JpaRepository<FundBaseEntity,Integer> {

}
