package com.thoongatechies.require.dm.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.spring.context.SpringManagedContext;
import com.thoongatechies.dmc.spec.SpecConfiguration;
import com.thoongatechies.require.dm.reactive.callback.AutowiringSpringBeanJobFactory;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * Created by mages_000 on 17-Jul-16.
 */
@Configuration
@ComponentScan(basePackages = {"com.thoongatechies.require.dm"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.thoongatechies.require.dm.config.*"))
@PropertySource("application-test.properties")
@Import({TestMongoConfig.class, SpecConfiguration.class})
@Profile("test")
public class ServiceTestJpaConfig {

    @Bean
    public HazelcastInstance newHz(ManagedContext managedContext){
        Config config = new XmlConfigBuilder().build();
        config.setManagedContext(managedContext);
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public ManagedContext managedContext(){
        return new SpringManagedContext();
    }

    @Bean
    @Qualifier("THREADPOOL")
    public Reactor createThreadPoolExector(Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.THREAD_POOL)
                .get();
    }


    @Bean
    @Qualifier("RINGBUFFER")
    public Reactor createRingBufferReactor(Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.RING_BUFFER)
                .get();
    }


    @Bean
    @Qualifier("WORKQUEUE")
    public Reactor createWorkQueueReactor(Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.WORK_QUEUE)
                .get();
    }

    @Bean
    @Qualifier("EVENTLOOP")
    public Reactor createEventLoopReactor(reactor.core.Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(reactor.core.Environment.EVENT_LOOP).get();
    }

    @Bean
    public Reactor createReactor(Environment env){
        return Reactors.reactor()
                .env(env)
                .dispatcher(Environment.THREAD_POOL)
                .get();
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext){
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean scheduler(JobFactory jobFactory){
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(quartzProperties());
        factory.setJobFactory(jobFactory);
        return factory;
    }

    private Properties quartzProperties(){
        Properties prop = new Properties();
        prop.setProperty("org.quartz.jobStore.class","org.quartz.simpl.RAMJobStore");
        prop.setProperty("org.quartz.threadpool.threadCount", "10");
        prop.setProperty("org.quartz.threadpool.class", "org.quartz.simpl.SimpleTheadPool");
        return prop;
    }

    @Bean
    public MBeanServer mBeanServer(){
        return ManagementFactory.getPlatformMBeanServer();
    }

    @Bean @ConditionalOnMissingBean(Environment.class)
    public Environment env(){
        return new Environment();
    }
}
