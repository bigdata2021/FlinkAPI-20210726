package com.bigdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private Integer userId;
    private Integer money;
    private Long createTime;
    private Timestamp time;
}
