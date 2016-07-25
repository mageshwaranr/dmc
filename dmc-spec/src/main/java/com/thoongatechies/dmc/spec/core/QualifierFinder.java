package com.thoongatechies.dmc.spec.core;

import com.thoongatechies.dmc.spec.vo.Trigger;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by mages_000 on 3/26/2016.
 */
public interface QualifierFinder extends Serializable{

    Map<String,Object>  findQualifier(Trigger evt, Spec spec);

}
