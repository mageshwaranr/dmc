package com.thoongatechies.require.dm.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Configuration
@EnableReactor("reactor")
@Import({DMMongoDaoConfig.class,HazelcastServerConfig.class})
@Profile("!test")
public class DependencyAppConfig {

    @Inject
    Environment environment;

    @Bean
    @Qualifier("THREADPOOL")
    public Reactor createThreadpoolReactor(reactor.core.Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(reactor.core.Environment.THREAD_POOL).get();
    }

    @Bean
    @Qualifier("RINGBUFFER")
    public Reactor createRingBufferReactor(reactor.core.Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(reactor.core.Environment.RING_BUFFER).get();
    }
    @Bean
    @Qualifier("WORKQUEUE")
    public Reactor createWorkQueueReactor(reactor.core.Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(reactor.core.Environment.WORK_QUEUE).get();
    }

    @Bean
    @Qualifier("EVENTLOOP")
    public Reactor createEventLoopReactor(reactor.core.Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(reactor.core.Environment.EVENT_LOOP).get();
    }


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
