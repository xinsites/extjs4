package com.xinsite.core.shiro.verify;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 重写shiro登录验证
 *
 * @author zhangxiaxin
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    //private final static Logger LOGGER = LoggerFactory.getLogger(CredentialsMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
//        UsernamePasswordToken authcToken = (UsernamePasswordToken) token;
//        Object in_password = String.valueOf(authcToken.getPassword()); //用户输入密码
//        Object db_password = getCredentials(info);                  //数据库密码
//        String server_pwd = Md5Utils.md5(db_password + ValidCodeUtils.getValidateCode());
//        //System.out.println("获得数据库中的密码:" + server_pwd);
//        return server_pwd.equals(in_password);
        return true;  //SimpleAuthenticationInfo存的是原密码，之前已经验证了
    }
}
