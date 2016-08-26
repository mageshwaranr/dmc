package com.thoongatechies.require.dm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * Created by mages_000 on 16-Jul-16.
 */
@SpringBootApplication
@Profile("!test")
public class DMCRunner {

    public static void main(String[] args) {
        SpringApplication.run(new Object[]{DMCRunner.class}, args);
    }


}
