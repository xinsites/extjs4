package com.xinsite.task;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xinsite.common.uitls.codec.RSAUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 随机生成RSA密钥对任务
 * create by zhangxiaxin
 */
@Component
@EnableScheduling
@EnableAsync
public class AsynKeyPairTask {
    protected final static Log log = LogFactory.getLog(AsynKeyPairTask.class);

    @Async
    @Scheduled(initialDelay = 60 * 1000, fixedRate = 480 * 1000)  //间隔480秒
    public void addKeyPairTask() {
        try {
            if (RSAUtils.getKeyPairCount() == 0) {
                for (int i = 0; i < 10; i++) RSAUtils.addKeyPair();
            } else {
                RSAUtils.addKeyPair();
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    @Async
    @Scheduled(initialDelay = 60 * 1000, fixedRate = 600 * 1000)  //间隔10分钟
    public void removeKeyPairTask() {
        RSAUtils.removeKeyPair();
    }

}
