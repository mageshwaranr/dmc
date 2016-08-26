package com.thoongatechies.require.dm.reactive.reactor;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.Reactor;
import reactor.event.selector.Selectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
public class EventListenerManager implements ApplicationContextAware {

    @Inject
    @Qualifier("THREADPOOL")
    private Reactor threadPool;

    @Inject
    @Qualifier("WORKQUEUE")
    private Reactor workQueue;

    private ApplicationContext ctxt;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctxt = applicationContext;
    }

    @PostConstruct
    public void registerEventListeners(){
        workQueue.on(Selectors.T(EventEntity.class), ctxt.getBean(IncomingEventListener.class));
        threadPool.on(Selectors.T(CallbackDataEntity.class), ctxt.getBean(CallbackDataEventListener.class));
    }
}
