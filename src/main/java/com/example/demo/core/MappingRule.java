package com.example.demo.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.yamlpath.YamlExpressionParser;

@Component
public class MappingRule {
	
	private List<Function<Object, Object>> genericRules = new ArrayList<>();
	private Consumer<Object> consumer = null;
	private Object materializedSource = null;
	
	@JsonProperty("mappingName")
    private String mappingName;
    
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("target")
    private String target;
	
	@JsonProperty("rule")
	private List<String> rules;

	
	public Object getMaterializedSource() {
		return materializedSource;
	}
	
	public List<Function<Object, Object>> getGenericRules() {
		return genericRules;
	}
	
	public Consumer<Object> getConsumer() {
		return consumer;
	}
	
	public String getMappingName() {
		return mappingName;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		System.out.println("setSource called  " + source);
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;

	}
	
	private void createRules(YamlExpressionParser catalogParser) {

       for (String className : rules) {
            try {
            	className = "com.example.demo.rules." + className;
                Class<?> clazz = Class.forName(className);
                if (Function.class.isAssignableFrom(clazz)) {
                    Function<Object, Object> function = (Function<Object, Object>) clazz.getDeclaredConstructor().newInstance();
                    genericRules.add(function);
                } else if (Consumer.class.isAssignableFrom(clazz)) {
                    Constructor<?> constructor = clazz.getDeclaredConstructor(Object.class, YamlExpressionParser.class);
                    consumer = (Consumer<Object>) constructor.newInstance(target, catalogParser);
                }
            } catch (ReflectiveOperationException e) {
                // Handle any exceptions during class loading
                e.printStackTrace();
            }
        }
	}


	public void initMe(YamlExpressionParser oasParser, Environment environment, YamlExpressionParser catalogParser) {
	try {	System.out.println("after properties set");
		System.out.println("Source: " + source);
        if (source.startsWith("env.")) {
            String propertyName = source.substring("env.".length());
            materializedSource = environment.getProperty(propertyName);
        } else {
        	materializedSource = oasParser.readSingle(source);
        }	
        System.out.println("M - Source: " + materializedSource);
		createRules(catalogParser); } catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("source: " + source);
		buffer.append("target: " + target);
		buffer.append("mapping name: " + mappingName);
		buffer.append("rule: " + rules);
		return buffer.toString();
	}
	

}
