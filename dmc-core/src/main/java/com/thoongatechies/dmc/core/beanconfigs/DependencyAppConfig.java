package com.thoongatechies.dmc.core.beanconfigs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Configuration
@ComponentScan("com.thoongatechies.dmc.core")
public class DependencyAppConfig {


    @Bean
    public SchedulerFactoryBean scheduler(Optional<DataSource> dataSource, SpringBeanJobFactory jobFactory){
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        dataSource.ifPresent(factoryBean::setDataSource);
        factoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        factoryBean.setQuartzProperties(quartzProperties());
        factoryBean.setJobFactory(jobFactory);
        return factoryBean;
    }

    private Properties quartzProperties() {
        Properties props = new Properties();
        props.setProperty("org.quartz.jobStore.class","org.quartz.simpl.RAMJobStore");
//        props.setProperty("org.quartz.jobStore.driverDelegateClass","org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
//        props.setProperty("org.quartz.jobStore.tablePrefix","QRTZ_");
        return props;
    }

}
