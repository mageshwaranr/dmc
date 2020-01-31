package com.thoongatechies.dmc.core.startup;

import com.thoongatechies.dmc.core.service.EventRuleRelation;
import com.thoongatechies.dmc.core.service.RecoverStateService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by mages_000 on 6/4/2016.
 */
public class ServiceStartup implements ApplicationListener<ContextRefreshedEvent> {

    private boolean canLoad = true;
    RecoverStateService service;
    EventRuleRelation relation;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(canLoad){
            populateBeans(event.getApplicationContext());
            relation.initializeCache();
            service.recoverState();
            canLoad = false;
        }
    }

    private void populateBeans(ApplicationContext applicationContext) {
        service = applicationContext.getBean(RecoverStateService.class);
        relation = applicationContext.getBean(EventRuleRelation.class);
    }
}
