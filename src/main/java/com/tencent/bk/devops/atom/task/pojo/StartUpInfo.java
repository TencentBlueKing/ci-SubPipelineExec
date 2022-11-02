package com.tencent.bk.devops.atom.task.pojo;

import lombok.Data;

@Data
public class StartUpInfo<T> {

    /**
     * 子流水线参数名
     */
    private String id;

    /**
     * 子流水线参数值
     */
    T name;
}
