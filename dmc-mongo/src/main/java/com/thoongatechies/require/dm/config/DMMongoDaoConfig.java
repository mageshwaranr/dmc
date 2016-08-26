package com.thoongatechies.require.dm.config;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Configuration
@ComponentScan(basePackages = {"com.thoongatechies.require.dm.dao"})
@EnableMongoRepositories(basePackages = {"com.thoongatechies.require.dm.dao"})
@Profile("!test")
public class DMMongoDaoConfig {

    String CANON_HOS_PROP = "CANONICALIZE_HOST_NAME";
    String MONGO_DOT_REPLACEMENT_KEY = "__dot__";

//    @Value("${mongo.db.principal}")
//    private String principal;
    @Value("${mongo.db.hostnames:localhost}")
    private String locations;
    @Value("${mongo.db.name:dmc}")
    private String dbName;

    @Bean
    public MongoClient mongo() {
        MongoClientOptions ops = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.REPLICA_ACKNOWLEDGED)
                .readPreference(ReadPreference.primaryPreferred())
                .build();
        return new MongoClient(mongoLocations(),
//                mongoCredentials(),
                ops);
    }

//    private List<MongoCredential> mongoCredentials() {
//        return Arrays.asList(MongoCredential.createGSSAPICredential(principal).withMechanismProperty(CANON_HOS_PROP, true));
//    }

    private List<ServerAddress> mongoLocations() {
        String[] hosts = locations.split(",");
        List<ServerAddress> addresses = new ArrayList<>();
        for (String host : hosts) {
            try {
                addresses.add(new ServerAddress(host));
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Couldnt not parse address " + host, e);
            }
        }
        return addresses;
    }


    @Bean
    public MongoDbFactory mongoDbFactory(MongoClient mongo){
        return new SimpleMongoDbFactory(mongo,dbName);
    }

    @Bean
    public MappingMongoConverter mongoConverter(MongoDbFactory factory, MongoMappingContext context){
        DbRefResolver ref = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(ref, context);
        mappingMongoConverter.setMapKeyDotReplacement(MONGO_DOT_REPLACEMENT_KEY);
        return mappingMongoConverter;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory dbFactory, MappingMongoConverter mongoConverter){
        return new MongoTemplate(dbFactory,mongoConverter);
    }

    @Bean
    public LocalValidatorFactoryBean dataValidator(){
        return new LocalValidatorFactoryBean();
    }
}
