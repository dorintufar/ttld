package com.egleey.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "com.egleey" })
@PropertySource({
        "classpath:parameters.properties",
        "classpath:icecast-server.properties",
        "classpath:icecast-streams.properties"
})
public class PropertiesRegister {
}
