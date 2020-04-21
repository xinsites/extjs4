package com.xinsite.core.model.system;

import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.bll.system.BLL_Config;

import java.util.List;

/**
 * 系统参数配置缓存Model
 * create by zhangxiaxin
 */
public class SysConfigModel {

    /**
     * 组织机构Id
     */
    private Integer orgId;

    /**
     * 登录错误次数(0不限制)，可修改
     */
    private Integer loginErrors;

    /**
     * 登录错误锁定时间(分钟)，可修改
     */
    private Integer loginLocked;

    /**
     * 默认分页数
     */
    private Integer pageSize;

    /**
     * 导出Excel最大记录数
     */
    private Integer excelMaxCount;

    /**
     * 固定栏目到面板最大数目
     */
    private Integer maxFixedTabs;

    /**
     * 历史数据分表记录数(万)
     */
    private Integer subTableRecords;

    /**
     * 用户允许登录的最大session，
     * <1：无限制；
     * =1：只能登录一台电脑；
     * >1：可以在多个地方同时登录；
     */
    private Integer maxSession;

    /**
     * 前端静态文件加载版本号
     */
    private Integer fileLoadVersion;

    /**
     * 导航提醒栏目(右上角提醒栏目)
     */
    private List<Integer> remind_items = null;

    public SysConfigModel(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getLoginErrors() {
        if (loginErrors == null) {
            loginErrors = BLL_Config.getConfigValue(orgId, "login_errors", 5);
        }
        return loginErrors;
    }

    public void setLoginErrors(Integer loginErrors) {
        this.loginErrors = loginErrors;
    }

    public Integer getLoginLocked() {
        if (loginLocked == null) {
            //首次获取，从数据库获取
            loginLocked = BLL_Config.getConfigValue(orgId, "login_locked", 30);
        }
        return loginLocked;
    }

    public void setLoginLocked(Integer loginLocked) {
        this.loginLocked = loginLocked;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            pageSize = BLL_Config.getConfigValue(orgId, "pageSize", 40);
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getExcelMaxCount() {
        if (excelMaxCount == null) {
            excelMaxCount = BLL_Config.getConfigValue(orgId, "excel_max_count", 10000);
        }
        return excelMaxCount;
    }

    public void setExcelMaxCount(Integer excelMaxCount) {
        this.excelMaxCount = excelMaxCount;
    }

    public Integer getMaxFixedTabs() {
        if (maxFixedTabs == null) {
            maxFixedTabs = BLL_Config.getConfigValue(orgId, "max_fixed_tabs", 3);
        }
        return maxFixedTabs;
    }

    public void setMaxFixedTabs(Integer maxFixedTabs) {
        this.maxFixedTabs = maxFixedTabs;
    }

    public Integer getMaxSession() {
        if (maxSession == null) {
            maxSession = BLL_Config.getConfigValue(orgId, "max_session", 0);
        }
        return maxSession;
    }

    public void setMaxSession(Integer maxSession) {
        this.maxSession = maxSession;
    }

    public Integer getFileLoadVersion() {
        if (fileLoadVersion == null) {
            fileLoadVersion = BLL_Config.getConfigValue(orgId, "file_load_version", 0);
        }
        return fileLoadVersion;
    }

    public void setFileLoadVersion(Integer fileLoadVersion) {
        this.fileLoadVersion = fileLoadVersion;
    }

    public List<Integer> getRemindItems() {
        if (remind_items == null) {
            String item_ids = BLL_Config.getConfigValue(orgId, "bar_remind_items", "");
            remind_items = StringUtils.splitToList(item_ids);
        }
        return remind_items;
    }

    public void setRemindItems(String item_ids) {
        this.remind_items = StringUtils.splitToList(item_ids);
    }

    public Integer getSubTableRecords() {
        if (subTableRecords == null) {
            subTableRecords = BLL_Config.getConfigValue(orgId, "subtable_records", 0);
        }
        return subTableRecords;
    }

    public void setSubTableRecords(Integer subTableRecords) {
        this.subTableRecords = subTableRecords;
    }
}
