/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.r2dbc")
@Component
public class DatabaseProperties {
   
    private String mockUrl;
    private String mockUsername;
    private String mockPassword;
    private String mockDdlSchemaDir;
    private String mockHost;
    private String mockPort;
    private String mockDatabase;
    
    private String pgUrl;
    private String pgUsername;
    private String pgPassword;
    private String pgDdlSchemaDir;
    private String pgHost;
    private String pgPort;
    private String pgDatabase;
    
    private String prodUrl;
    private String prodUsername;
    private String prodPassword;
    private String prodDdlSchemaDir;
    private String prodHost;
    private String prodPort;
    private String prodDatabase;
    
    private ProfileSetting profileSetting;
    private Boolean setupMockUserOnly;
    private Boolean truncateMockData;
    private Boolean skipDataInit;
    private String databaseUsed;

    private String url;
    private String username;
    private String password;
    private String host;
    private String port;
    private String database;
    private String ddlSchemaDir;

    private Integer poolInitialSize = 8;
    private Integer poolMaxSize = 20;
    private Integer poolMinSize = 5;
    private Integer connTimeout = 2000;

    public static enum ProfileSetting {
        MOCK, PG, PROD
    }
    
    public void setupBaseDbProps(ProfileSetting ps) {
        profileSetting = ps;
        
        switch(profileSetting) {
            case ProfileSetting.PROD -> {
                this.url = this.prodUrl;
                this.host = this.prodHost;
                this.port = this.prodPort;
                this.username = this.prodUsername;
                this.password = this.prodPassword;
                this.database = this.prodDatabase;
                this.databaseUsed = this.prodDatabase;
                this.ddlSchemaDir = this.prodDdlSchemaDir;
            }
            case ProfileSetting.MOCK -> {
                this.url = this.mockUrl;
                this.host = this.mockHost;
                this.port = this.mockPort;
                this.username = this.mockUsername;
                this.password = this.mockPassword;
                this.database = this.mockDatabase;
                this.databaseUsed = this.mockDatabase;
                this.ddlSchemaDir = this.mockDdlSchemaDir;
            }
            default -> {
                this.url = this.pgUrl;
                this.host = this.pgHost;
                this.port = this.pgPort;
                this.username = this.pgUsername;
                this.password = this.pgPassword;
                this.database = this.pgDatabase;
                this.databaseUsed = this.pgDatabase;
                this.ddlSchemaDir = this.pgDdlSchemaDir;
            }
        }
    }
}
