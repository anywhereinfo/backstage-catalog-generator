package com.example.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import io.github.yamlpath.YamlExpressionParser;
import io.github.yamlpath.YamlPath;

@Component
public class Converter {

	String fileName = "backstage.yml";
	
	@Autowired 
	private RulesEngine rulesEngine;
	
	public void convert (String oasFile, String repoLocation) {
		Path oasDirectory = getDirectory(oasFile);
		
		String oas = readFile(oasFile);
		String catalog = readCatalog();
		YamlExpressionParser oasYaml = YamlPath.from(oas);
		YamlExpressionParser catalogYaml = YamlPath.from(catalog);
		rulesEngine.applyRules(oasYaml, catalogYaml);
		/**
		String apiName = oasYaml.readSingle("info.title");
		if (apiName.matches(".*\\s.*")) {
			apiName = apiName.replaceAll("\\s", "-");
		}

		catalogYaml.write("metadata.name", apiName); **/
		String catalogString = catalogYaml.dumpAsString();
		writeFile(oasDirectory, catalogString, constructNameFrom(extractFileName(oasFile)));
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
	
	private String readFile(String localOASFile) {
	       String oasFile = null;
			try {
	            // Read the file into a byte array
	            byte[] bytes = Files.readAllBytes(Paths.get(localOASFile));
	            
	            // Convert the byte array to a string using UTF-8 encoding
	            oasFile = new String(bytes, StandardCharsets.UTF_8);
	            
	            System.out.println(oasFile);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
           return oasFile;
	    }
	
	private String readCatalog() {
        String backstageFile = null;
		try {
            // Load the file from the resources folder inside the JAR
            ClassPathResource resource = new ClassPathResource(fileName);
            
            // Read the file into a string
            backstageFile = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            
            System.out.println(backstageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }	
		return backstageFile;
	}
	}

