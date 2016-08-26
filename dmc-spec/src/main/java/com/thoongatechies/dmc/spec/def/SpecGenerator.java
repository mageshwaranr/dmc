package com.thoongatechies.dmc.spec.def;

import java.net.URL;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface SpecGenerator<T> {

    Spec generate(T input, Map<String, URL> urlBindingMap);
}
