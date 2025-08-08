/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import com.sisllc.instaiml.config.DatabaseProperties.ProfileSetting;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${spring.r2dbc.pool.initial-size}")
    private Integer initialPoolSize;
    @Value("${spring.r2dbc.pool.max-size}")
    private Integer maxPoolSize;

    @Autowired
    protected DatabaseProperties dbProps;

    @Autowired
    private Environment env;
    
    private ProfileSetting profileSetting;
    
    @PostConstruct
    public void init() {
        log.debug("DatabaseConfig profiles {}", Arrays.toString(env.getActiveProfiles()));
        if (env.getActiveProfiles() == null || env.getActiveProfiles().length == 0 ||
            env.acceptsProfiles(ProfileMockConfig.MOCK_PROFILES)) {
           profileSetting = ProfileSetting.MOCK;
        } else if (env.acceptsProfiles(Profiles.of(ProfileProdConfig.PROD_PROFILE))) {
           profileSetting = ProfileSetting.PROD;
        } else {
           profileSetting = ProfileSetting.PG;
        }
        
        dbProps.setupBaseDbProps(profileSetting);
        log.debug("DatabaseConfig props {}", dbProps);
    }
    
    @Bean
    public ConnectionFactory connectionFactory() {
        log.debug("connectionFactory profiles {} propd {}", Arrays.toString(env.getActiveProfiles()), dbProps);

        PostgresqlConnectionFactory baseFactory = new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(dbProps.getHost())
                .port(Integer.parseInt(dbProps.getPort()))
                .database(dbProps.getDatabase())
                .username(dbProps.getUsername())
                .password(dbProps.getPassword())
                .build()
        );

        return new ConnectionPool(ConnectionPoolConfiguration.builder(baseFactory)
            .initialSize(dbProps.getPoolInitialSize())
            .maxSize(dbProps.getPoolMaxSize())
            .build()
        );

        /*
        if (profileSetting == ProfileSetting.PROD) {
            PostgresqlConnectionFactory baseFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                    .host(dbProps.getProdHost())
                    .port(Integer.parseInt(dbProps.getProdPort()))
                    .database(dbProps.getProdDatabase())
                    .username(dbProps.getProdUsername())
                    .password(dbProps.getProdPassword())
                    .build()
            );

            return new ConnectionPool(ConnectionPoolConfiguration.builder(baseFactory)
                .initialSize(initialPoolSize)
                .maxSize(maxPoolSize)
                .build()
            );
        }
        
        if (profileSetting == ProfileSetting.MOCK) {
            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(dbProps.getMockUrl())
                .mutate()
                .option(ConnectionFactoryOptions.USER, dbProps.getMockUsername())
                .option(ConnectionFactoryOptions.PASSWORD, dbProps.getMockPassword())
                .build();
            return ConnectionFactories.get(options);
        }

        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(dbProps.getPgUrl())
            .mutate()
            .option(ConnectionFactoryOptions.USER, dbProps.getPgUsername())
            .option(ConnectionFactoryOptions.PASSWORD, dbProps.getPgPassword())
            .build();
        return ConnectionFactories.get(options);
        // */
    }
    
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        log.debug("r2dbcEntityTemplate profiles {} propd {}", Arrays.toString(env.getActiveProfiles()), dbProps);
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
