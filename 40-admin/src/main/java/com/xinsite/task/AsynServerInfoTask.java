package com.xinsite.task;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xinsite.core.utils.web.domain.Server;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 获取服务器信息任务
 * create by zhangxiaxin
 */
@Component
@EnableScheduling
@EnableAsync
public class AsynServerInfoTask {
    protected final static Log log = LogFactory.getLog(AsynServerInfoTask.class);

    @Async
    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 24 * 60 * 60 * 1000)  //间隔60秒
    public void calcServerInfo() {
        try {
            Server.initServerInfo();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

}
