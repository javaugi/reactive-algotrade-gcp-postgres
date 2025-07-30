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
    private String ddlSchemaDir;
    private Boolean setupMockUserOnly;
    private Boolean truncateMockData;
    private String databaseUsed;
    
    public static enum ProfileSetting {
        MOCK, PG, PROD
    }
    
    public void setupBaseDbProps(ProfileSetting ps) {
        profileSetting = ps;
        
        switch(profileSetting) {
            case ProfileSetting.PROD -> {
                ddlSchemaDir = prodDdlSchemaDir;
                databaseUsed = this.prodDatabase;
            }
            case ProfileSetting.MOCK -> {
                ddlSchemaDir = mockDdlSchemaDir;
                databaseUsed = this.mockDatabase;               
            }
            default -> {
                ddlSchemaDir = pgDdlSchemaDir;
                databaseUsed = this.pgDatabase;                
            }
        }
    }
}
