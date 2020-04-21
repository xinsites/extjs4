package com.xinsite.core.shiro.service;

import com.xinsite.common.constant.ShiroConstant;
import com.xinsite.common.uitls.idgen.IdGenerate;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.model.user.LoginUser;
import com.xinsite.core.bll.permission.BLL_UserPower;
import com.xinsite.core.utils.user.UserUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 用户信息变更缓存更改
 */
@Component
public class UserCacheService {
    @Autowired
    private CacheManager cacheManager;

    /**
     * 指定用户信息更新标记，查看用户的Principal中是否有该标记，没有变更信息
     */
    private Cache<Integer, String> userInfoCache;

    /**
     * 用户剔除栏目id集合缓存
     */
    private Cache<Integer, String> delItemIdsCache;

    @PostConstruct
    public void init() {
        userInfoCache = cacheManager.getCache(ShiroConstant.CHANGE_USER_INFO_CACHE);
        delItemIdsCache = cacheManager.getCache(ShiroConstant.USER_DEL_ITEM_CACHE);
    }

    /**
     * 清空指定用户剔除栏目缓存，使用获取最新的
     */
    public void clearUserDeleteItemIds(int user_id) {
        delItemIdsCache.remove(user_id);
    }

    /**
     * 获取指定用户剔除栏目缓存
     */
    public String getCacheRemoveItemIds(int user_id) {
        String del_item_ids = delItemIdsCache.get(user_id);
        if (del_item_ids == null) {
            del_item_ids = BLL_UserPower.getUserRemoveItemIds(user_id);
            del_item_ids = StringUtils.joinAsFilter(del_item_ids);
            delItemIdsCache.put(user_id, del_item_ids);
        }
        return del_item_ids;
    }

    /**
     * 指定用户更新登录信息
     */
    public void changeUserInfoFlag(int user_id) {
        userInfoCache.put(user_id, IdGenerate.buildUUID());
    }

    /**
     * 登录用户同步登录信息
     */
    public void syncFlag() {
        LoginUser loginUser = UserUtils.getLoginUser();
        String user_flag = userInfoCache.get(loginUser.getUserId());
        if (!StringUtils.isEmpty(user_flag)) {
            loginUser.setChangeUserFlag(user_flag);
        } else {
            loginUser.setChangeUserFlag("loaded");
        }
    }

    /**
     * 该登录用户是否需要更新用户信息
     */
    public boolean isChangeUserInfo(LoginUser loginUser) {
        if (userInfoCache.size() == 0) return false;
        String userInfoFlag = userInfoCache.get(loginUser.getUserId());
        if (StringUtils.isEmpty(userInfoFlag)) return false;
        boolean bool = userInfoFlag.equals(loginUser.getChangeUserFlag());
        if (bool == false) {
            loginUser.setChangeUserFlag(userInfoFlag);
        }
        return !bool;
    }


}
