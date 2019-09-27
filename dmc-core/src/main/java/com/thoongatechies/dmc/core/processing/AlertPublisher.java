package com.thoongatechies.dmc.core.processing;

import com.thoongatechies.dmc.core.entity.CallbackDataEntity;

/**
 * Created by mages_000 on 6/4/2016.
 */
public interface AlertPublisher {
    void alert(CallbackDataEntity callbackData, String message);
}
