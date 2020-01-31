package com.thoongatechies.dmc.core.entity;

import com.thoongatechies.dmc.spec.vo.SpecExecutionState;
import com.thoongatechies.dmc.spec.vo.TriggerGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mages_000 on 6/1/2016.
 */
//@Document(collection="RuleInstanceState")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"createdBy","lastUpdatedBy","createdOn","lastUpdatedOn"})
public class RuleInstanceStateEntity implements SpecExecutionState {
    public static final String ID_COLUMN = "_id";

    private String id, createdBy, lastUpdatedBy;
    private Date createdOn, lastUpdatedOn;
    private Set<TriggerGroup> pendingTriggerGroups = new HashSet<>();
    private Set<TriggerGroup> globalTriggerGroups = new HashSet<>();

    public void addTriggerGroup(TriggerGroup toBeAdded) {
        if(toBeAdded.isGlobalGroup()){
            this.globalTriggerGroups.add(toBeAdded);
        } else {
            this.pendingTriggerGroups.add(toBeAdded);
        }
    }

    @Override
    public void setTriggerGroupAsProcessed(TriggerGroup processed) {
        if(processed.isGlobalGroup()) {
            // you don't have to remove Global group. Needs recursive processing
        } else {
            pendingTriggerGroups.remove(processed);
        }
    }
}
