package com.xw.bughome.middleware.dynamic.thread.pool.sdk.trigger.job;

import cn.hutool.json.JSONUtil;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 线程池数据上报任务
 * Created by MaxWell on 2024/5/31 00:09
 */
public class ThreadPoolDataReportJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IRegistry registry;

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    public ThreadPoolDataReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        logger.info("动态线程池，上报线程池信息:{}", JSONUtil.toJsonStr(threadPoolConfigEntities));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("动态线程池，上报线程池配置参数:{}", JSONUtil.toJsonStr(threadPoolConfigEntity));
        }
    }
}
