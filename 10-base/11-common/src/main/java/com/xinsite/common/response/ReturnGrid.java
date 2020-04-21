package com.xinsite.common.response;


import com.google.gson.JsonArray;
import com.xinsite.common.uitls.extjs.JsonGrid;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Ajax请求返回的分页列表
 *
 * @author ZhangXiaXin
 */
@Component
public class ReturnGrid {

    private long totalProperty;
    private boolean state;
    private JsonArray root;

    public ReturnGrid() {
        this.totalProperty = 0;
        this.state = false;
        this.root = new JsonArray();
    }

    public ReturnGrid(JsonArray list) {
        this.totalProperty = list.size();
        this.state = true;
        this.root = list;
    }

    public ReturnGrid(JsonArray list, long totalCount) {
        this.totalProperty = totalCount;
        this.state = true;
        this.root = list;
    }

    /**
     * ExtJs,Grid分页列表需要的Json字符串
     */
    public String getGridJson() {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        this.state = true;
        return GsonUtils.toJson(this);
    }

    /**
     * ExtJs,Grid分页列表需要的Json字符串
     */
    public String getGridJson(JsonArray array) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        this.state = true;
        if (array == null) GsonUtils.toJson(this);
        this.totalProperty = array.size();
        this.root = array;
        return GsonUtils.toJson(this);
    }

    /**
     * ExtJs,Grid分页列表需要的Json字符串
     */
    public String getGridJson(JsonArray array, long count) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        this.state = true;
        if (array == null) GsonUtils.toJson(this);
        this.totalProperty = count;
        this.root = array;
        return GsonUtils.toJson(this);
    }

    /**
     * ExtJs,Grid分页列表需要的Json字符串
     */
    public String getGridJson(JsonArray array, long count, String[] field) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        if (array == null || count == 0) return "{totalProperty:0,state:true,root:[]}";
        String str = JsonGrid.getGridJson(array, field);
        return "{" + StringUtils.format("totalProperty:{0},state:true,root:{1}", count, str) + "}";
    }

    /**
     * ExtJs,Grid分页列表需要的Json字符串
     */
    public String getGridJson(JsonArray array, long count, String field) {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        String[] str = field.split(",");
        for (int i = 0; i < str.length; i++) {
            if (str[i].indexOf(".") > 0) {
                String[] temps = str[i].split(".");
                if (temps.length == 2) str[i] = temps[1];
                //str[i] = str[i].replace("a.", "").replace("b.", "");
            }
        }
        return getGridJson(array, count, str);
    }

    //失败
    public String getFailResult() {
        this.totalProperty = 0;
        this.state = false;
        this.root = new JsonArray();
        return GsonUtils.toJson(this);
    }

    //失败
    public String getTreeFail() {
        //if (ShiroUtils.isSessionOut()) return ReturnMap.getSessionOut();
        return "[]";
    }
}
