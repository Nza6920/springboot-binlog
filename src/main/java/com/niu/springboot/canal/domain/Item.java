package com.niu.springboot.canal.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiabin.xu
 * @descriptoion
 */
@Data
@Table(name = "sys_config")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String value;

    private BigDecimal price;

    @Column(name = "create_at")
    private Date create_at;


}
