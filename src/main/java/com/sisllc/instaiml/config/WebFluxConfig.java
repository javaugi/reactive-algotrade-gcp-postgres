/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;

@Slf4j
@Configuration
@EnableWebFlux // Might not be strictly needed if using Spring Boot WebFlux starter, but ensures WebFlux configuration.
public class WebFluxConfig implements WebFluxConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("WebFluxConfigurer addResourceHandlers");
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/static/"); // Note the nested 'static/'
        registry.addResourceHandler("/*.js")
            .addResourceLocations("classpath:/static/static/js/");
        registry.addResourceHandler("/*.css")
            .addResourceLocations("classpath:/static/static/css/");
        registry.addResourceHandler("/**") // For index.html and other root-level assets
            .addResourceLocations("classpath:/static/");
    }

}

/*
project file structure

reactive-pharm-az-cosmosdb/
└── frontend/
    └── buuld/
    └── public/
    └── src/
└── src/
    └── main/
        └── resources/
            └── static/
                ├── index.html                  
                ├── manifest.json               
                ├── logo192.png                 
                ├── favicon.ico                 
                ├── asset-manifest.json         
                └── static/                     
                    ├── css/
                    │   └── main.6e427bcd.css
                    └── js/
                        └── main.b21851af.js

file strcuture from unzip jar file

BOOT-INF/
    └── classes/
        └── static/
            ├── index.html                  
            ├── manifest.json               
            ├── logo192.png
            ├── favicon.ico                 
            ├── asset-manifest.json         
            └── static/                     
                ├── css/
                │   └── main.6e427bcd.css
                └── js/
                    └── main.b21851af.js

*/
