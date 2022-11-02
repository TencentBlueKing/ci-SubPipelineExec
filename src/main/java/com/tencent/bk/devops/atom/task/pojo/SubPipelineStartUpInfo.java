package com.tencent.bk.devops.atom.task.pojo;

import lombok.Data;

import java.util.List;

/**
 * 子流水线全局变量定义
 */
@Data
public class SubPipelineStartUpInfo {

    /**
     * 参数key值
     */
    private String key;

    /**
     * key值是否可以更改
     */
    private Boolean keyDisable;

    /**
     * key值前端组件类型
     */
    private String keyType;

    /**
     * key值获取方式
     */
    private String keyListType;

    /**
     * key值获取路径
     */
    private String keyUrl;

    private List<String> keyUrlQuery;

    /**
     * key值获取集合
     */
    private List<StartUpInfo> keyList;

    /**
     * key值是否多选
     */
    private Boolean keyMultiple;

    /**
     * 参数value值
     */
    Object value;

    /**
     * value值是否可以更改
     */
    private Boolean valueDisable;

    /**
     * value值前端组件类型
     */
    private String valueType;

    /**
     * value值获取方式
     */
    private String valueListType;

    /**
     * value值获取路径
     */
    private String valueUrl;

    private List<String> valueUrlQuery;

    /**
     * value值获取集合
     */
    private List<StartUpInfo> valueList;

    /**
     * value值是否多选
     */
    private Boolean valueMultiple;
}
