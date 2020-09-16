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
    @ExcelProperty(value = "序号",index = 0)
    private String unid;
    @ExcelProperty(value = "基金名称",index = 1)
    private String name;
    @ExcelProperty(value = "基金类型",index = 2)
    private String fundType;
    @ExcelProperty(value = "基金风险",index = 3)
    private String fundRisk;//截取 split("|")[1]
    @ExcelProperty(value = "基金范围",index = 4)
    private String fundScope;//截取 split("（")[0]
    @ExcelProperty(value = "基金经理人",index = 5)
    private String fundManager;//基金经理
    @ExcelProperty(value = "基金评级",index = 6)
    private String fundRank;//截取 jjpj4 中的数字
    @ExcelProperty(value = "开始时间",index = 7)
    private String startDate;
}
