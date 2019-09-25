package com.thoongatechies.require.dm.config;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.net.UnknownHostException;

/**
 * Created by mages_000 on 17-Jul-16.
 */
@Configuration
@ComponentScan("com.thoongatechies.require.dm.dao")
@EnableMongoRepositories("com.thoongatechies.require.dm.dao")
public class TestMongoConfig {

    @Bean
    public Mongo mongo() throws UnknownHostException {
//        Fongo fongo = new Fongo("Fake-Mongo");
//        return fongo.getMongo();
        return new MongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException{
        return new MongoTemplate(mongo(),"TestDB");
    }

    @Bean
    public LocalValidatorFactoryBean dataValidator(){
        return new LocalValidatorFactoryBean();
    }
}
