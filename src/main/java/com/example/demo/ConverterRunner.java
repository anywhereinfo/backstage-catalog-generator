package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ConverterRunner implements ApplicationRunner {

    @Autowired
    private Converter converter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> repoLocation = args.getOptionValues("repoLocation");
        List<String> oasFile = args.getOptionValues("oasFile");
        converter.convert(oasFile.get(0), repoLocation.get(0));
    }
}
