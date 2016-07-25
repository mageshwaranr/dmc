package com.thoongatechies.dmc.spec.core.dsl;

import com.thoongatechies.dmc.spec.core.Spec;
import com.thoongatechies.dmc.spec.core.SpecBuilder;
import com.thoongatechies.dmc.spec.core.SpecGenerator;
import com.thoongatechies.dmc.spec.core.dsl.builder.DslBuilder;
import org.mvel2.MVEL;

import javax.inject.Named;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * Created by mages_000 on 5/27/2016.
 */
@Named
public class SpecGeneratorImpl implements SpecGenerator<String> {

    private Map<String,Spec> cachedSpec = new HashMap<>();
//
//    @Inject
//    private RestTemplate restTemplate;

    @Override
    public Spec generate(String input, Map<String, URL> urlBindingMap) {
        return cachedSpec.computeIfAbsent(input, key -> {
            SpecBuilder builder = (SpecBuilder) MVEL.eval(key, singletonMap("Matcher",new DslBuilder()));
            return builder.withUserProvidedSpec(key).withUrlBindingMap(urlBindingMap).build();
        });
    }
}
