package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.github.yamlpath.YamlExpressionParser;

@Component
public class RulesEngine {

	@Autowired
	private List<MappingRule> mappingRules;
    private final Environment environment;

    public RulesEngine(List<MappingRule> mappingRules, Environment environment) {
        this.mappingRules = mappingRules;
        this.environment = environment;
    }

    public void applyRules(YamlExpressionParser sourceYaml, YamlExpressionParser targetYaml) {
        for (MappingRule rule : mappingRules) {
            String source = rule.getSource();
            if (source.startsWith("env.")) {
                String propertyName = source.substring("env.".length());
                source = environment.getProperty(propertyName);
            } else {
            	source = sourceYaml.readSingle(source);
            }
            	

            if (rule.getRules() != null) {
                for (String ruleName : rule.getRules()) {
                    if (ruleName.equals("NoWhitespace")) {
                        source = source.replaceAll("\\s", "-");
                    }
                    if (ruleName.equals("CopyField")) {
                        targetYaml.write(rule.getTarget(), source);
                    }
                }
            } 
        }
    }
}
