package org.bot0ff.config;

import org.bot0ff.util.JsonProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JsonProcessor getJsonProcessor() {
        return new JsonProcessor();
    }
}
