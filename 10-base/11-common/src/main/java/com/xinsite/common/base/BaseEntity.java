package com.xinsite.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 */
public class BaseEntity implements Serializable {

    /**
     * 主键
     */
    private long idleaf;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序号
     */
    private long serialcode;

    /**
     * 置顶号
     */
    private int position;

    /**
     * 置顶时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date position_time;

    /**
     * 创建人Id
     */
    private int create_uid;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    /**
     * 修改人Id
     */
    private int modify_uid;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modify_time;

    /**
     * 栏目Id
     */
    private int itemid;

    /**
     * 创建人机构Id
     */
    private int org_id;

    /**
     * 创建人部门Id
     */
    private int dept_id;

    /**
     * 是否删除
     */
    private int isdel;


}
