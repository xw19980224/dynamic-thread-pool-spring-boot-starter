package com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain;

import cn.hutool.json.JSONUtil;
import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池服务
 * Created by MaxWell on 2024/5/24 13:30
 */
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final String applicationName;
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVO = new ArrayList<>(threadPoolBeanNames.size());
        for (String threadPoolBeanName : threadPoolBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolBeanName);
            ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity(applicationName, threadPoolBeanName);
            threadPoolConfigEntity.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolConfigEntity.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolConfigEntity.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolConfigEntity.setPoolSize(threadPoolExecutor.getPoolSize());
            threadPoolConfigEntity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            threadPoolConfigEntity.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolConfigEntity.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            threadPoolVO.add(threadPoolConfigEntity);
        }
        return threadPoolVO;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (null == threadPoolExecutor) return new ThreadPoolConfigEntity(applicationName, threadPoolName);

        // 线程池配置数据
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolConfigEntity.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigEntity.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigEntity.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigEntity.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolConfigEntity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolConfigEntity.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigEntity.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSONUtil.toJsonStr(threadPoolConfigEntity));
        }

        return threadPoolConfigEntity;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) return;
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (null == threadPoolExecutor) return;

        int maximumPoolSize = threadPoolConfigEntity.getMaximumPoolSize();
        int corePoolSize = threadPoolConfigEntity.getCorePoolSize();

        // CorePoolSize 小于等于 MaximumPoolSize 否则发出警告
        if (maximumPoolSize < corePoolSize) {
            logger.error("动态线程池，变更配置出错(最大线程数不能小于核心线程数):{}", threadPoolConfigEntity);
        }

        // 设置参数 「调整核心线程数和最大线程数」
        // 更改时注意设置值的顺序，始终满足CorePoolSize 小于等于 MaximumPoolSize
        if (corePoolSize < threadPoolExecutor.getMaximumPoolSize()) {
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
        } else {
            threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
            threadPoolExecutor.setCorePoolSize(corePoolSize);
        }

    }
}
