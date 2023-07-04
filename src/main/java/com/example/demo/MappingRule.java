package com.example.demo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MappingRule {
	
	@JsonProperty("mappingName")
    private String mappingName;
    
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("target")
    private String target;
	
	@JsonProperty("rule")
	private List<String> rules;

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
	
	

    
    
}
