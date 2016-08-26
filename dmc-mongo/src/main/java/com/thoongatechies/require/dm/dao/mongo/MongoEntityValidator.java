package com.thoongatechies.require.dm.dao.mongo;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by mages_000 on 6/3/2016.
 */
@Named
public class MongoEntityValidator extends AbstractMongoEventListener<Object> {

    private Validator validator;

    @Inject
    public MongoEntityValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        super.onBeforeSave(event);
        Set<ConstraintViolation<Object>> violations = validator.validate(event.getSource());
        if (violations.size() > 0){
            StringBuilder sb = new StringBuilder("");
            violations.forEach(sb::append);
            throw new ConstraintViolationException(sb.toString(),violations);
        }
    }
}
