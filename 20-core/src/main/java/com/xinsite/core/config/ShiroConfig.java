package com.xinsite.core.config;

import com.xinsite.core.shiro.filter.KickoutFilter;
import com.xinsite.core.shiro.filter.LogoutFilter;
import com.xinsite.core.shiro.filter.OnlineUserFilter;
import com.xinsite.core.shiro.filter.SessionFilter;
import com.xinsite.core.shiro.realm.UserRealm;
import com.xinsite.core.shiro.service.UserCacheService;
import com.xinsite.core.shiro.session.OnlineSessionDAO;
import com.xinsite.core.shiro.session.OnlineSessionFactory;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置加载
 */
@Configuration
public class ShiroConfig {
    // Session超时时间，单位为毫秒（默认30分钟）
    @Value("${shiro.session.timeout}")
    private int timeout;

    // 相隔多久检查一次session的有效性，单位毫秒，默认就是10分钟
    @Value("${shiro.session.validationInterval}")
    private int validationInterval;

    // 验证码开关
    @Value("${shiro.user.captchaEnabled}")
    private boolean captchaEnabled;

    // 设置Cookie的域名
    @Value("${shiro.cookie.domain}")
    private String domain;

    // 设置cookie的有效访问路径
    @Value("${shiro.cookie.path}")
    private String path;

    // 设置HttpOnly属性
    @Value("${shiro.cookie.httpOnly}")
    private boolean httpOnly;

    // 设置记住我Cookie的过期时间(天)
    @Value("${shiro.cookie.rememberExpireDays}")
    private int rememberExpireDays;

    // 登录地址
    @Value("${shiro.user.loginUrl}")
    private String loginUrl;

    // 权限认证失败地址
    @Value("${shiro.user.unauthorizedUrl}")
    private String unauthorizedUrl;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    @Autowired
    private UserCacheService userCacheService;

    /**
     * Shiro过滤器配置
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);  //Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setLoginUrl(loginUrl);                //认证失败跳转到登录页面，默认Web工程根目录下的"/login.jsp"页面
        //shiroFilterFactoryBean.setSuccessUrl(mainUrl);               //登录成功后要跳转的链接
        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);  //权限认证失败，跳转到指定页面

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>(); //拦截器配置
        //authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/javascript/**", "anon");
        filterChainDefinitionMap.put("/styles/**", "anon");
        filterChainDefinitionMap.put("/error/**", "anon");
        filterChainDefinitionMap.put("/publicKey", "anon");
        filterChainDefinitionMap.put("/imageKaptcha", "anon");
        filterChainDefinitionMap.put("/anon/**", "anon");
        filterChainDefinitionMap.put("/login.html", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/login", "anon"); //登录
        filterChainDefinitionMap.put("/logout", "anon,logout"); //退出
        filterChainDefinitionMap.put("/api", "anon");        //手机接口等
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        //user表示访问该地址的用户是身份验证通过或RememberMe登录的都可以
        filterChainDefinitionMap.put("/**", "user,kickout,onlineUser");

        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("kickout", kickoutFilter());
        filters.put("user", sessionFilter());
        filters.put("logout", logoutFilter());  //退出加过滤器
        filters.put("onlineUser", onlineUserFilter());

        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 同一个用户多设备登录限制
     */
    public KickoutFilter kickoutFilter() {
        KickoutFilter kickoutFilter = new KickoutFilter();
        kickoutFilter.setCacheManager(getEhCacheManager());
        kickoutFilter.setSessionManager(sessionManager());
        kickoutFilter.setKickoutUrl(loginUrl); // 被踢出后重定向到的地址；
        return kickoutFilter;
    }

    /**
     * 退出过滤器
     */
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setCacheManager(getEhCacheManager());
        logoutFilter.setLoginUrl(loginUrl);
        return logoutFilter;
    }

    /**
     * 请求认证过滤器
     */
    public SessionFilter sessionFilter() {
        SessionFilter sessionFilter = new SessionFilter();
        sessionFilter.setLoginUrl(loginUrl);
        sessionFilter.setOnlineSessionDAO(onlineSessionDAO);
        sessionFilter.setChangeService(userCacheService);
        return sessionFilter;
    }

    /**
     * 在线用户处理过滤器
     */
    @Bean
    public OnlineUserFilter onlineUserFilter() {
        OnlineUserFilter onlineSessionFilter = new OnlineUserFilter();
        return onlineSessionFilter;
    }

    /**
     * 配置核心安全事务管理器
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(userRealm);            //设置自定义realm.
        manager.setRememberMeManager(rememberMeManager());  //配置记住我
        manager.setCacheManager(getEhCacheManager());  //配置 ehcache缓存管理器
        manager.setSessionManager(sessionManager()); //配置自定义session管理
        return manager;
    }

    /**
     * 配置会话管理器，设定会话超时及保存
     *
     * @return
     */
    @Bean(name = "sessionManager")
    public SessionManager sessionManager() {
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        manager.setCacheManager(getEhCacheManager());    //加入缓存管理器
        manager.setDeleteInvalidSessions(true);          //删除过期的session
        manager.setGlobalSessionTimeout(timeout * 60 * 1000L);  //设置全局session超时时间
        manager.setSessionIdUrlRewritingEnabled(false);         //去掉 JSESSIONID
        manager.setSessionValidationSchedulerEnabled(true);     //是否定时检查session
        manager.setSessionDAO(sessionDAO());  // 自定义SessionDao
        manager.setSessionFactory(sessionFactory()); // 自定义sessionFactory
        return manager;
    }

    /**
     * 自定义sessionDAO会话
     */
    @Bean
    public OnlineSessionDAO sessionDAO() {
        OnlineSessionDAO sessionDAO = new OnlineSessionDAO();
        return sessionDAO;
    }

    /**
     * 自定义sessionFactory会话
     */
    @Bean
    public OnlineSessionFactory sessionFactory() {
        OnlineSessionFactory sessionFactory = new OnlineSessionFactory();
        return sessionFactory;
    }

    /**
     * 浏览器 cookie 属性设置
     */
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(rememberExpireDays * 24 * 60 * 60);
        return cookie;
    }

    /**
     * 记住我
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("3Av2hmFLAs0BTA3Kprsd6E=="));
        return cookieRememberMeManager;
    }

    /**
     * 自定义Realm
     */
    @Bean
    public UserRealm userRealm(EhCacheManager cacheManager) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCacheManager(cacheManager);
        return userRealm;
    }

    /**
     * 缓存管理器 使用Ehcache实现
     */
    @Bean
    public EhCacheManager getEhCacheManager() {
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.getCacheManager("com.xinsite" + domain);
        EhCacheManager em = new EhCacheManager();
        if (cacheManager == null) {
            em.setCacheManager(new net.sf.ehcache.CacheManager(getCacheManagerConfigFileInputStream()));
            return em;
        } else {
            em.setCacheManager(cacheManager);
            return em;
        }
    }

    /**
     * 返回配置文件流 避免ehcache配置文件一直被占用，无法完全销毁项目重新部署
     */
    protected InputStream getCacheManagerConfigFileInputStream() {
        String configFile = "classpath:ehcache/ehcache-shiro.xml";
        InputStream inputStream = null;
        try {
            inputStream = ResourceUtils.getInputStreamForPath(configFile);
            byte[] b = IOUtils.toByteArray(inputStream);
            InputStream in = new ByteArrayInputStream(b);
            return in;
        } catch (IOException e) {
            throw new ConfigurationException(
                    "无法获取cachemanagerconfigfile的输入流 [" + configFile + "]", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


}
