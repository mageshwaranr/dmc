package com.thoongatechies.dmc.spec.service;

import com.thoongatechies.dmc.spec.core.Spec;
import com.thoongatechies.dmc.spec.vo.SpecExecutionState;
import com.thoongatechies.dmc.spec.vo.Trigger;
import com.thoongatechies.dmc.spec.vo.TriggerGroup;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Created by mageshwaranr on 7/24/2016.
 */
public interface SpecService {

    Spec parseSpec(String spec, Map<String,URL> bindingMap);

    Collection<TriggerGroup> evaluateSpec(Trigger trigger, SpecExecutionState state, Spec spc);

}
