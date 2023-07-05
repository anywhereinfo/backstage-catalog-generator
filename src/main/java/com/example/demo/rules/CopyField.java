package com.example.demo.rules;

import java.util.function.Consumer;

import io.github.yamlpath.YamlExpressionParser;

public class CopyField implements Consumer<Object> {

	private YamlExpressionParser catalogParser;
    private String target = null;

    public CopyField(Object target, YamlExpressionParser catalogParser) {
        this.target = (String)target;
        this.catalogParser = catalogParser;
    }

    @Override
    public void accept(Object source) {
    	System.out.println(String.format("Source: '%s' and Target: '%s'", source, target));
        catalogParser.write(target, source);
        System.out.println(catalogParser.dumpAsString());
    }
}

