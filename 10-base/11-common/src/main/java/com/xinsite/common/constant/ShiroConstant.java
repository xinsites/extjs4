package com.xinsite.common.constant;

/**
 * Shiro通用常量
 *
 * @author zhangxiaxin
 */
public class ShiroConstant {
    /**
     * 当前登录的用户
     */
    public static final String CURRENT_USER = "current_user";

    /**
     * 错误key
     */
    public static String ERROR = "errorMsg";

    /**
     * 当前在线会话
     */
    public static String ONLINE_SESSION = "online_session";

    /**
     * 登录错误记录缓存
     */
    public static final String LOGIN_ERRORS_CACHE = "login_errors_cache";

    /**
     * 登录用户踢出缓存
     */
    public static final String LOGIN_KICKOUT_CACHE = "login_kickout_cache";

    /**
     * 在线用户最后插入数据库时间
     */
    public static final String ONLINE_LAST_DB = "online_last_inser_time";

    /**
     * 用户信息需要更新记录缓存
     */
    public static final String CHANGE_USER_INFO_CACHE = "change_user_info_cache";

    /**
     * 用户剔除栏目id集合缓存
     */
    public static final String USER_DEL_ITEM_CACHE = "user_del_item_ids_cache";

}
