package com.xinsite.core.utils.log;

import com.xinsite.common.uitls.Global;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.common.enums.system.LogTypeEnum;
import com.xinsite.common.uitls.PropsUtils;
import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.dal.bean.Keys;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.core.utils.user.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogUtils {

    /**
     * 新增登录日志(登录与退出)
     */
    public static void addLogByLogin(String action_type, String log_message, String username, String loginIp) {
        LoginUser loginUser = UserUtils.getLoginUser();
        loginUser.setLoginIp(loginIp);
        String log_fun = action_type.equals("登录") ? "用户登录" : "用户退出";
        if (action_type.equals("记住我")) {
            log_fun = "记住我登录";
            action_type = "登录";
        }
        log_message = log_message.replace("...", "");
        Map<String, Object> ht = new HashMap();
        ht.put("log_type", LogTypeEnum.登录日志.getIndex());
        ht.put("log_fun", log_fun);
        ht.put("login_name", username);
        ht.put("action_type", action_type); //登录与退出
        ht.put("log_result", "用户名".equals(log_message) ? "成功" : "失败");
        ht.put("log_message", String.format("%s[%s]", log_message, username));

        new SaveLogThread(loginUser, ht).start();
    }

    /**
     * 新增登录日志(登录与退出)
     */
    public static void addLogByLogin(String action_type, String username, String loginIp) {
        LogUtils.addLogByLogin(action_type, "用户名", username, loginIp);
    }

    /**
     * 新增访问日志
     */
    public static void addLogByBrowse(int item_id) {
        LoginUser loginUser = UserUtils.getLoginUser();
        Map<String, Object> ht = new HashMap();
        ht.put("log_type", LogTypeEnum.访问日志.getIndex());
        ht.put("action_type", "访问");  //操作类型
        ht.put("log_result", "成功");
        new SaveLogThread(loginUser, ht, new Keys("ItemId", item_id)).start();
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(String log_fun, String Result) {
        LogUtils.addOperateLog(0, log_fun, Result, "");
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(String log_fun, String Result, String log_message) {
        LogUtils.addOperateLog(0, log_fun, Result, log_message);
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(int item_id, String log_fun, String Result) {
        LogUtils.addOperateLog(item_id, log_fun, Result, "");
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(int item_id, String log_fun, long idleaf) {
        LogUtils.addOperateLog(item_id, log_fun, idleaf > 0 ? "成功" : "失败", "");
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(int item_id, String log_fun, String Result, String log_message) {
        LoginUser loginUser = UserUtils.getLoginUser();
        Map<String, Object> ht = new HashMap();
        ht.put("log_type", LogTypeEnum.操作日志.getIndex());
        ht.put("log_fun", log_fun);    //系统功能:操作说明
        ht.put("action_type", "操作");  //操作类型
        ht.put("log_result", Result);   //成功、失败
        ht.put("log_message", log_message);
        new SaveLogThread(loginUser, ht, new Keys("item_id", item_id)).start();
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(boolean isAdd, int item_id, long idleaf) {
        String item_name = LogUtils.getItemName(item_id);
        LogUtils.addOperateLog(isAdd, idleaf > 0 ? "成功" : "失败", item_name);
    }

    /**
     * 新增操作日志
     */
    public static void addOperateLog(boolean isAdd, String log_result, String log_message) {
        LoginUser loginUser = UserUtils.getLoginUser();
        Map<String, Object> ht = new HashMap();
        ht.put("log_type", LogTypeEnum.操作日志.getIndex());
        ht.put("log_fun", (isAdd ? "信息新增" : "信息修改"));    //系统功能:操作说明
        ht.put("action_type", "操作");  //操作类型
        ht.put("log_result", log_result);
        ht.put("log_message", log_message);
        new SaveLogThread(loginUser, ht).start();
    }

    /**
     * 新增异常日志
     */
    public static void addExceptionLog(String log_fun, String log_message) {
        LoginUser loginUser = UserUtils.getLoginUser();
        Map<String, Object> ht = new HashMap();
        ht.put("log_type", LogTypeEnum.异常日志.getIndex());
        ht.put("log_fun", log_fun);     //系统功能:操作说明
        ht.put("action_type", "异常");  //操作类型
        ht.put("log_result", "失败");
        ht.put("log_message", log_message);  //异常说明

        new SaveLogThread(loginUser, ht).start();
    }

    /**
     * 获取栏目名
     */
    public static String getItemName(int item_id) {
        String QuerySql = "select item_name from sys_menu where item_id=" + item_id;
        Object obj = DBFunction.executeScalar(QuerySql);
        return obj == null ? "" : obj.toString();
    }

    /**
     * 保存日志线程
     */
    public static class SaveLogThread extends Thread {
        private static final Logger log = LoggerFactory.getLogger(SaveLogThread.class);

        private LoginUser loginUser;
        private Map<String, Object> map;
        private Keys[] keys;

        //异步保存日志
        public SaveLogThread(LoginUser loginUser, Map<String, Object> map, Keys... keys) {
            this.loginUser = loginUser;
            this.map = map;
            this.keys = keys;
            if (keys == null) {
                keys = new Keys[]{};
            }
        }

        @Override
        public void run() {
            if (!Global.getConfig("config.is_write_log").equals("true")) return;
            try {
                int log_type = NumberUtils.strToInt(map.get("log_type"));
                if (log_type == LogTypeEnum.访问日志.getIndex()) {
                    if (keys.length > 0) {
                        int item_id = NumberUtils.strToInt(keys[0].value);
                        if (item_id > 0) {
                            map.put("log_fun", "栏目访问");
                            map.put("log_message", LogUtils.getItemName(item_id));
                        }
                    }
                } else if (log_type == LogTypeEnum.操作日志.getIndex()) {
                    for (Keys key : keys) {
                        if (key.key.equalsIgnoreCase("item_id")) {
                            int item_id = NumberUtils.strToInt(key.value);
                            if (item_id > 0) {
                                String log_message = "";
                                if (map.containsKey("log_message")) log_message = map.get("log_message").toString();
                                String item_name = LogUtils.getItemName(item_id);
                                if (!StringUtils.isEmpty(log_message)) log_message += ",";
                                if (!StringUtils.isEmpty(item_name)) {
                                    log_message += "栏目：" + item_name;
                                } else {
//                                    String VisitPosition = LogUtils.GetVisitPosition(item_id);
//                                    log_message += "访问地址：" + VisitPosition;
                                }
                                map.put("log_message", log_message);
                            }
                        }
                    }
                }
                map.put("org_id", loginUser.getOrgId());
                map.put("log_ip", loginUser.getLoginIp());
                map.put("user_id", loginUser.getUserId());
                map.put("dept_id", loginUser.getDeptId());
                map.put("create_time", DateUtils.getDateTime());
                DBFunction.insertByTbName(map, "sys_log");
            } catch (Exception ex) {
                log.error("访问日志写入出错：", ex);
            }
        }
    }
}
