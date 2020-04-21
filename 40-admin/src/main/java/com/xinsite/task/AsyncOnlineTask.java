package com.xinsite.task;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xinsite.core.shiro.service.OnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 删除在线用户任务
 * create by zhangxiaxin
 */
@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
public class AsyncOnlineTask {
    protected final static Log log = LogFactory.getLog(AsyncOnlineTask.class);

    @Autowired
    private OnlineService onlineService;

    @Async
    @Scheduled(initialDelay = 60 * 1000, fixedRate = 100 * 1000)  //间隔100秒
    public void deleteOnlineTask() {
        try {
            if (onlineService != null) onlineService.batchDeleteOnline();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }


}
