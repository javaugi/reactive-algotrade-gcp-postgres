package com.sisllc.instaiml;

import com.sisllc.instaiml.config.ProfileMockConfig;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Slf4j
@ComponentScan(basePackages = MyReactiveApplication.PACKAGE_SCAN_BASE)
@EnableR2dbcRepositories(basePackages = MyReactiveApplication.PACKAGE_SCAN_BASE_REPO)
@EnableConfigurationProperties
@SpringBootApplication
public class MyReactiveApplication implements EnvironmentAware {
    protected static final String PACKAGE_SCAN_BASE = "com.sisllc.instaiml";
    protected static final String PACKAGE_SCAN_BASE_REPO = "com.sisllc.instaiml.repository";
    
    @Override
    public void setEnvironment(Environment environment) {
        log.info("MyReactiveApplication setEnvironment Spring Boot {}", SpringBootVersion.getVersion());  
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if (env.getActiveProfiles() == null || env.getActiveProfiles().length == 0 ||
            env.acceptsProfiles(ProfileMockConfig.MOCK_PROFILES)) {
            env.addActiveProfile("mock");
        }
        log.info("MyReactiveApplication setEnvironment profiles {}", Arrays.toString(env.getActiveProfiles()));  
    }    
    
    // com.sisllc.instaiml.MyReactiveApplication
	public static void main(String[] args) {
        log.info("MyReactiveApplication main Spring Boot {}", SpringBootVersion.getVersion());  
		SpringApplication.run(MyReactiveApplication.class, args);
	}
    
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			log.debug("MyReactiveApplication commandLineRunner Beans provided by Spring Boot {}", SpringBootVersion.getVersion());  
            
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
                log.debug(beanName);
			}
			log.debug("MyReactiveApplication commandLineRunner Server is ready: Spring Boot {}", SpringBootVersion.getVersion());            
		};
	}    
}
