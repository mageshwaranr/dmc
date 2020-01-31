package com.thoongatechies.dmc.core.resource;

import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Named;
import javax.ws.rs.ApplicationPath;

/**
 * Created by mages_000 on 6/1/2016.
 */
@Named
@ApplicationPath("/rest")
public class JaxRsResourceConfig extends ResourceConfig {

    public JaxRsResourceConfig() {
        register(DependencyDefinitionResource.class);
        register(DependencyInstanceResource.class);
    }
}
