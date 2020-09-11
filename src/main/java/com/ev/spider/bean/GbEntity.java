package com.ev.spider.bean;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
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
public class GbEntity extends BaseRowModel {
    @Id //主键
    @ExcelProperty(value = "序号",index = 0)
    @GeneratedValue
    private Integer id;
    @ExcelProperty(value = "总登记号",index = 1)
    private String registerId;
    @ExcelProperty(value = "标准名",index = 2)
    @Column(length = 400)
    private String name;
    @ExcelProperty(value = "版本号",index = 3)
    private String version;
    @ExcelProperty(value = "发布部门",index = 4)
    private String department;
    @ExcelProperty(value = "发放专业",index = 5)
    private String major;
    @ExcelProperty(value = "更新后标准",index = 6)
    @Column(length = 400)
    private String updatedName;
    @ExcelProperty(value = "更新后版本号",index = 7)
    private String updatedVersion;
    @ExcelProperty(value = "状态",index = 8)
    private String status;//状态
    @ExcelProperty(value = "dataType",index = 9)
    private String dataType;//数据类型
//    private String updated;//更新标记
//    private String stopFlag;//暂停标记

    @ExcelProperty(value = "secondSpiderPath",index = 10)
    private String secondSpiderPath;//第二次爬取路径
    @ExcelProperty(value = "thirdSpiderPath",index = 11)
    private String thirdSpiderPath;//第三次爬取路径
}
