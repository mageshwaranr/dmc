package com.thoongatechies.dmc.core.processing;

import com.thoongatechies.dmc.core.entity.CallbackDataEntity;
import com.thoongatechies.dmc.core.entity.EventEntity;

/**
 * Created by mages_000 on 6/4/2016.
 */
public interface EventPublisher {

    void postIncomingEvent(EventEntity evt);

    void postCallbackEvent(CallbackDataEntity callbackData);

}
