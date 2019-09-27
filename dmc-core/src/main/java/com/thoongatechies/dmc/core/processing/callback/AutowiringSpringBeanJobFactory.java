package com.thoongatechies.dmc.core.processing.callback;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mages_000 on 6/4/2016.
 */
@Named
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanJobFactory;

    @Inject
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanJobFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        beanJobFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
