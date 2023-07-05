package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.example.demo.core.GenericRulesEngine;
import com.example.demo.core.MappingRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.github.yamlpath.YamlExpressionParser;
import io.github.yamlpath.YamlPath;

@Component
public class Converter {

    @Value("classpath:rules.yml")
    private Resource rulesResource;
    
    @Value("classpath:backstage.yml")
    private Resource backstageResource;
    
	String fileName = "backstage.yml";

	@Autowired
	GenericRulesEngine rulesEngine;
	
	@Autowired
	Environment environment;
	
	private YamlExpressionParser catalogParser;
	

	public void convert (String oasFile, String repoLocation) throws IOException {
		YamlExpressionParser oasParser = getOasParser(oasFile);
		catalogParser = getCatalogParser();
		Path oasDirectory = getDirectory(oasFile);
		List<MappingRule> mappingRules = getMappingRules(oasParser, catalogParser);
		rulesEngine.execute(mappingRules);
		String catalogString = catalogParser.dumpAsString();
		writeFile(oasDirectory, catalogString, constructNameFrom(extractFileName(oasFile)));
	}
	
    public List<MappingRule> getMappingRules(YamlExpressionParser oasParser, YamlExpressionParser catalogParser) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        List<MappingRule> mappingRules;
        try (InputStream inputStream = rulesResource.getInputStream()) {
            mappingRules = objectMapper.readValue(inputStream, new TypeReference<List<MappingRule>>() {
            });
        } 
        for (MappingRule rule : mappingRules)
        	rule.initMe(oasParser, environment, catalogParser);
        return mappingRules;
    }
    
    private YamlExpressionParser getCatalogParser() {
    	YamlExpressionParser catalogParser = null;
     	try {
    			catalogParser = YamlPath.from(StreamUtils.copyToString(backstageResource.getInputStream(), StandardCharsets.UTF_8));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
     	return catalogParser;
    }
	
	private String constructNameFrom(String oasName) {
		return "backstage-" + oasName + ".yml";
	}
	private String extractFileName(String oasFile) {
	       	Path path = Paths.get(oasFile);

	        // Get the file name without extension
	        String fileName = path.getFileName().toString();
	        int extensionIndex = fileName.lastIndexOf('.');
	        return fileName.substring(0, extensionIndex);
	}
	
	private void writeFile(Path oasDirectory, String catalogString, String catalogFileName) {

        Path filePath = oasDirectory.resolve(catalogFileName);

        try {

            // Write the content to the file
            Files.write(filePath, catalogString.getBytes());

            System.out.println("File stored successfully at: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while storing the file: " + e.getMessage());
        }		
	}
	
	
	private Path getDirectory (String oasFile) {
		Path oasDirectory = Paths.get(oasFile);
		return oasDirectory.getParent();
	}
	
    public YamlExpressionParser getOasParser(@Value("${oasFile}") String oasFile) {
    	System.out.println("*********************************************************GetOASPArser called");
		String oas = readFile(oasFile);
		YamlExpressionParser parser = YamlPath.from(oas);
		if (parser == null)
			System.out.println("***********************PARSER NULL");
		return parser;
    }
	
	private String readFile(String localOASFile) {
	       String oasFile = null;
			try {
	            // Read the file into a byte array
	            byte[] bytes = Files.readAllBytes(Paths.get(localOASFile));
	            
	            // Convert the byte array to a string using UTF-8 encoding
	            oasFile = new String(bytes, StandardCharsets.UTF_8);
	            

	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        return oasFile;
	    }
	
	}

