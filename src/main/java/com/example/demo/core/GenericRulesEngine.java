package com.example.demo.core;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class GenericRulesEngine {
	

    public void execute(List<MappingRule> rules) {
  
        Object intermediateResult = null;
        List<Function<Object, Object>> genericRules = null;
        Consumer<Object> consumer = null;
         
        for (MappingRule rule : rules) {
        	if (rule.getGenericRules() != null) {
        		genericRules = rule.getGenericRules();
        		intermediateResult = applyRules(genericRules, rule.getMaterializedSource());
        	} 
        	if (rule.getConsumer() != null) {
        		consumer = rule.getConsumer();
        		consumer.accept(intermediateResult);
        	}

        }
    }
    
    private Object applyRules(List<Function<Object, Object>> genericRules, Object source) {
    	Object intermediateResult = source;
    	for (Function<Object, Object> rule : genericRules) {
    		intermediateResult =   rule.apply(intermediateResult);
    	}
    	return intermediateResult;
    }
    
}
