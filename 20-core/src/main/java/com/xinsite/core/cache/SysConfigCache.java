package com.xinsite.core.cache;

import com.xinsite.common.uitls.lang.ValueUtils;
import com.xinsite.core.model.system.SysConfigModel;
import com.xinsite.core.utils.user.UserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysConfigCache {
    /**
     * 模拟数据库
     */
    private static final Map<Integer, SysConfigModel> config = new HashMap();

    /**
     * 初始化数据
     */
    static {
        config.put(1, new SysConfigModel(1));
    }

    public static SysConfigModel getSysConfigBean(int org_id) {
        if (!config.containsKey(org_id)) config.put(org_id, new SysConfigModel(org_id));
        return config.get(org_id);
    }

    public static SysConfigModel getSysConfigBean() {
        int org_id = UserUtils.getOrgId();
        if (!config.containsKey(org_id)) config.put(org_id, new SysConfigModel(org_id));
        return config.get(org_id);
    }

    public static int getMaxSession() {
        return getSysConfigBean().getMaxSession();
    }

    public static int getFileLoadVersion() {
        return getSysConfigBean().getFileLoadVersion();
    }

    public static int getLoginLocked() {
        return getSysConfigBean().getLoginLocked();
    }

    public static int getLoginLocked(int org_id) {
        return getSysConfigBean(org_id).getLoginLocked();
    }

    public static int getLoginErrors(int org_id) {
        return getSysConfigBean(org_id).getLoginErrors();
    }

    public static int getPageSize() {
        return getSysConfigBean().getPageSize();
    }

    public static int getMaxFixedTabs() {
        return getSysConfigBean().getMaxFixedTabs();
    }

    public static int getExcelMaxCount() {
        return getSysConfigBean().getExcelMaxCount();
    }

    public static List<Integer> getRemindItems() {
        return getSysConfigBean().getRemindItems();
    }

    public static int getSubTableRecords() {
        return getSysConfigBean().getSubTableRecords();
    }

    public static void setConfigValue(String config_key, String value) {
        SysConfigModel configBean = getSysConfigBean();
        if (config_key.equalsIgnoreCase("login_locked")) {
            configBean.setLoginLocked(ValueUtils.tryParse(value, 0, 30));
        } else if (config_key.equalsIgnoreCase("login_errors")) {
            configBean.setLoginErrors(ValueUtils.tryParse(value, 0, 5));
        } else if (config_key.equalsIgnoreCase("page_size")) {
            configBean.setPageSize(ValueUtils.tryParse(value, 0, 40));
        } else if (config_key.equalsIgnoreCase("excel_max_count")) {
            configBean.setExcelMaxCount(ValueUtils.tryParse(value, 0, 10000));
        } else if (config_key.equalsIgnoreCase("max_fixed_tabs")) {
            configBean.setMaxFixedTabs(ValueUtils.tryParse(value, 0, 3));
        } else if (config_key.equalsIgnoreCase("max_session")) {
            configBean.setMaxSession(ValueUtils.tryParse(value, 0, 0));
        } else if (config_key.equalsIgnoreCase("file_load_version")) {
            configBean.setFileLoadVersion(ValueUtils.tryParse(value, 0, 0));
        } else if (config_key.equalsIgnoreCase("bar_remind_items")) {
            configBean.setRemindItems(value);
        } else if (config_key.equalsIgnoreCase("subtable_records")) {
            configBean.setSubTableRecords(ValueUtils.tryParse(value, 0, 100));
        }
    }
}
