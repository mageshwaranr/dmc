package com.thoongatechies.require.dm.reactive;

import com.thoongatechies.require.dm.entity.CallbackDataEntity;

/**
 * Created by mages_000 on 6/4/2016.
 */
public interface AlertPublisher {
    void alert(CallbackDataEntity callbackData, String message);
}
