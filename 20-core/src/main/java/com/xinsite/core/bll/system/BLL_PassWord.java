package com.xinsite.core.bll.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xinsite.common.uitls.MessageUtils;
import com.xinsite.common.uitls.codec.Md5Utils;
import com.xinsite.common.uitls.gson.GsonUtils;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.utils.user.ShiroUtils;
import com.xinsite.dal.bean.DBParameter;
import com.xinsite.dal.dbhelper.DBFunction;
import com.xinsite.mybatis.datasource.master.entity.SysUser;

import java.util.List;

/**
 * 用户密码相关
 * create by zhangxiaxin
 */
public class BLL_PassWord {

    /**
     * 初始化用户密码
     */
    public static void initPassword() throws Exception {
        JsonArray array = DBFunction.executeJsonArray("select user_id,pwd_salt from sys_user");
        if (array != null) {
            String level_password = BLL_PassWord.getLevelPassword("111111");
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = GsonUtils.getObject(array, i);
                int user_id = GsonUtils.tryParse(object, "user_id", 0);
                String pwd_salt = IdGenerate.buildUUID();
                if (user_id > 0) {
                    String password = BLL_PassWord.getUserPassword(level_password, pwd_salt);
                    String sql = "update sys_user set pwd_salt='{1}',password='{2}' where user_id={0};";
                    DBFunction.executeNonQuery(StringUtils.format(sql, user_id, pwd_salt, password));
                }
            }
        }
    }

    /**
     * 获取第一层用户密码
     *
     * @param password 用户实际密码
     */
    public static String getLevelPassword(String password) {
        password = Md5Utils.md5(password).toUpperCase();
        return Md5Utils.md5(password).toLowerCase();
    }

    /**
     * 获取加密的用户密码
     *
     * @param level_password 第一层用户密码
     * @param pwd_salt       密码盐，uuid保证唯一
     */
    public static String getUserPassword(String level_password, String pwd_salt) {
        String password = level_password + pwd_salt;
        return Md5Utils.md5(password).toLowerCase();
    }

    /**
     * 设置用户密码
     *
     * @param user_id        用户Id
     * @param level_password 第一层用户密码
     */
    public static String getUserPassword(int user_id, String level_password) {
        String pwd_salt = BLL_User.getFieldByUser(user_id, "pwd_salt", "");
        return BLL_PassWord.getUserPassword(level_password, pwd_salt);
    }

    /**
     * 检查用户密码是否正确
     *
     * @param user_id        用户Id
     * @param level_password 第一层用户密码
     */
    public static boolean validatePassword(int user_id, String level_password) {
        String pwd_salt = BLL_User.getFieldByUser(user_id, "pwd_salt", "");
        String password = BLL_User.getFieldByUser(user_id, "password", "");
        String user_password = BLL_PassWord.getUserPassword(level_password, pwd_salt);
        return password.equals(user_password);
    }

    /**
     * 设置用户重置密码
     */
    public static boolean setUserPassword(String user_ids, String config_key) throws Exception {
        String password = BLL_Config.getConfigValue(config_key, "111111");
        password = Md5Utils.md5(password);
        String level_password = BLL_PassWord.getLevelPassword(password);
        List<Integer> list = StringUtils.splitToList(user_ids);
        for (int user_id : list) {
            if (config_key.equals("reset_password")) {
                ShiroUtils.kickoutUser(user_id, MessageUtils.message("user.password.reset"));  //重置密码用户下线
            }
            String strSql = "update sys_user set pwd_salt=@pwd_salt,password=@password where user_id=" + user_id;
            String pwd_salt = IdGenerate.buildUUID();  //重置密码盐
            String user_password = BLL_PassWord.getUserPassword(level_password, pwd_salt);
            DBFunction.executeNonQuery(strSql
                    , new DBParameter("@pwd_salt", pwd_salt)
                    , new DBParameter("@password", user_password));
        }
        return true;
    }
}
