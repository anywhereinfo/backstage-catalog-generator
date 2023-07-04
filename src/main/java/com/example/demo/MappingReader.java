package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class MappingReader {

    @Value("classpath:rules.yml")
    private Resource rulesResource;

    @Bean
    public List<MappingRule> getMappingRules() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = rulesResource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<MappingRule>>() {
            });
        }
    }
}
