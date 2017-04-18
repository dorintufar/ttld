package com.egleey.config;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;

@Configuration
@ComponentScan(basePackages = { "com.egleey.controller", "com.egleey" })
public class WebConfig extends WebMvcConfigurerAdapter {
    private final ServletContext servletContext;

    @Autowired
    public WebConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Bean
    public Loader templateLoader(){
        return new ServletLoader(servletContext);
    }

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("256MB");
        factory.setMaxRequestSize("256MB");
        return factory.createMultipartConfig();
    }

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder()
                .loader(this.templateLoader())
                .extension(springExtension())
                .cacheActive(false)
                .build();
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix("");
        viewResolver.setPebbleEngine(pebbleEngine());
        return viewResolver;
    }
}
