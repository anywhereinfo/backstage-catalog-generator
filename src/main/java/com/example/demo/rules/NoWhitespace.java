package com.example.demo.rules;

import java.util.function.Function;

public class NoWhitespace implements Function<Object, Object> {

    @Override
    public Object apply(Object input) {
        if (input instanceof String) {
            String source = (String) input;
                source = source.replaceAll("\\s", "-");
                return source;
        }
        return input;
    }
}

