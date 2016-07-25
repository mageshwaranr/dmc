package com.thoongatechies.dmc.spec;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
@Configuration
@ComponentScan
public class SpecConfiguration {

//    @Bean
//    public RestTemplate callbackRestTemplate(Optional<CustomAuthInterceptor> interceptor){
//        RestTemplate template = new RestTemplate();
//        interceptor.ifPresent(i -> template.setInterceptors(Collections.singletonList(i)));
//        return template;
//    }

}
