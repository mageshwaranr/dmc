package com.thoongatechies.require.dm.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastProperties;
import com.hazelcast.spring.context.SpringManagedContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.stream.Stream;

/**
 * Created by mages_000 on 09-Jun-16.
 */
@Configuration
@PropertySource("classpath:config/hazelcast.properties")
public class HazelcastServerConfig {

    @Bean
    public SpringManagedContext newSpringManagedContext(){
        return new SpringManagedContext();
    }

    @Bean
    public HazelcastInstance newHazelcastInstance(){
        return Hazelcast.getOrCreateHazelcastInstance(newHazelcastConfig());
    }

    @Value("${hz.name:DMC}")
    private String name;
    @Value("${hz.group.name:primary}")
    private String groupName;
    @Value("${hz.group.password:let_me_in}")
    private String groupPassword;
    @Value("${hz.network.port:6543}")
    private int networkPort;
    @Value("${hz.network.members:localhost}")
    private String members;

    private Config newHazelcastConfig() {
        Config config = new Config();
        config.setInstanceName(name);
        GroupConfig groupConfig = new GroupConfig(groupName,groupPassword);
        config.setGroupConfig(groupConfig);
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort(networkPort);
        networkConfig.setPortAutoIncrement(true);
        config.setNetworkConfig(networkConfig);
        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(true);
        Stream.of(members.split(",")).forEach( member -> join.getTcpIpConfig().addMember(member));
        return config;
    }

}
