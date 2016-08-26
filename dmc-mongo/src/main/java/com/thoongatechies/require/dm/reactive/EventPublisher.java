package com.thoongatechies.require.dm.reactive;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;
import com.thoongatechies.require.dm.entity.EventEntity;
import com.thoongatechies.require.dm.entity.EventInstanceEntity;

/**
 * Created by mages_000 on 6/4/2016.
 */
public interface EventPublisher {

    void postIncomingEvent(EventEntity evt);

    void postCallbackEvent(CallbackDataEntity callbackData);

}
