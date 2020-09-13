package com.ev.spider.bean.fund;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 14:26
 */
@Entity
@Data
public class FundBaseEntity {
    @Id //主键
    private String unid;
    private String name;
}
