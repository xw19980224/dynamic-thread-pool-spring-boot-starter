package com.xw.bughome.middleware.dynamic.thread.pool.sdk.trigger.listener;

import cn.hutool.json.JSONUtil;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 动态线程池变更监听
 * Created by MaxWell on 2024/6/3 09:46
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final IRegistry registry;

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    public ThreadPoolConfigAdjustListener(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("动态线程池，调整线程池配置。 线程池名称：{} 核心数：{} 最大线程数：{}",
                threadPoolConfigEntity.getThreadPoolName(),
                threadPoolConfigEntity.getPoolSize(),
                threadPoolConfigEntity.getMaximumPoolSize()
        );
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);

        // 更新后上报最新数据
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);

        // 更新线程池配置
        ThreadPoolConfigEntity currentThreadPoolConfigEntity =
                dynamicThreadPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(currentThreadPoolConfigEntity);
        logger.info("动态线程池，上报线程池配置：{}", JSONUtil.toJsonStr(threadPoolConfigEntity));
    }
}
