package com.niu.springboot.canal.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiabin.xu
 * @descriptoion
 */
@Data
@Table(name = "sys_dict_data")
public class SysDictData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 排序
     */

    private Integer sort;
    /**
     * 字典名称
     */

    private String label;
    /**
     * 字典值
     */

    private String value;
    /**
     * 字典类型
     */

    private String dictType;
    /**
     * 字典描述
     */

    private String description;
    /**
     * 禁用状态
     */

    private String disableFlag;
    /**
     * 过滤字符
     */

    private String filterKeywords;


    private String createBy;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 逻辑删除位
     */

    private Integer deleteFlag = 0;


}
