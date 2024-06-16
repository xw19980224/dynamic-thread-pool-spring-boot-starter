package com.xw.bughome.middleware.dynamic.thread.pool.sdk.registry;

import com.xw.bughome.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心接口
 * Created by MaxWell on 2024/5/30 23:54
 */
public interface IRegistry {

    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
