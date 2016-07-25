package com.thoongatechies.dmc.spec.vo;


import java.util.HashSet;
import java.util.Set;


/**
 * Created by mageshwaranr on 7/24/2016.
 */
public class SpecExecutionState {

    private Set<TriggerGroup> pendingTriggerGroups = new HashSet<>();
    private Set<TriggerGroup> globalTriggerGroups = new HashSet<>();

    public Set<TriggerGroup> getPendingTriggerGroups() {
        return pendingTriggerGroups;
    }

    public void setPendingTriggerGroups(Set<TriggerGroup> pendingTriggerGroups) {
        this.pendingTriggerGroups = pendingTriggerGroups;
    }

    public Set<TriggerGroup> getGlobalTriggerGroups() {
        return globalTriggerGroups;
    }

    public void setGlobalTriggerGroups(Set<TriggerGroup> globalTriggerGroups) {
        this.globalTriggerGroups = globalTriggerGroups;
    }

    public void addTriggerGroup(TriggerGroup toBeAdded) {
        if(toBeAdded.isGlobalGroup()){
            this.globalTriggerGroups.add(toBeAdded);
        } else {
            this.pendingTriggerGroups.add(toBeAdded);
        }
    }

}
