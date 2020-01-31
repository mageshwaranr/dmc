package com.thoongatechies.dmc.spec.vo;


import java.util.Set;


/**
 * Created by mageshwaranr on 7/24/2016.
 */
public interface SpecExecutionState {

    Set<TriggerGroup> getPendingTriggerGroups();

    Set<TriggerGroup> getGlobalTriggerGroups();

    void addTriggerGroup(TriggerGroup toBeAdded);

    void setTriggerGroupAsProcessed(TriggerGroup processed);

}
