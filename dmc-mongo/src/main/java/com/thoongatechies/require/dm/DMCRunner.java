package com.thoongatechies.require.dm;

import com.thoongatechies.require.dm.config.DependencyAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Created by mages_000 on 16-Jul-16.
 */
@SpringBootApplication
@Import(DependencyAppConfig.class)
@Profile("!test")
public class DMCRunner {

    public static void main(String[] args) {
        SpringApplication.run(new Object[]{DMCRunner.class}, args);
    }


}
