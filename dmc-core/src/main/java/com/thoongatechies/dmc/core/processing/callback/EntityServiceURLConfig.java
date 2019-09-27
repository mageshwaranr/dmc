package com.thoongatechies.dmc.core.processing.callback;

import javax.inject.Named;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mages_000 on 6/2/2016.
 */
@Named
public class EntityServiceURLConfig {

    public void clearConfig(){

    }

    public Map<String, URL> getConfig() {
        return new HashMap<>();
    }
}
