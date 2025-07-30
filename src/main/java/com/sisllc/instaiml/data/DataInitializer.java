/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.config.DatabaseProperties;
import com.sisllc.instaiml.config.DatabaseProperties.ProfileSetting;
import com.sisllc.instaiml.service.InsurancePricingAnalyticalService;
import io.r2dbc.spi.ConnectionFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner{
    private static final String TRUNC_TABLE = "TRUNCATE TABLE %s CASCADE";
    
    @Autowired
    protected DatabaseProperties dbProps;
    @Autowired 
    private ConnectionFactory connFactory;    
    @Autowired
    private DataGeneratorService dataGenService; 
    @Autowired
    private InsurancePricingAnalyticalService anylyticalService;
    
    private DatabaseClient dbClient;
    private String databaseUsed;
    
    @Override
    public void run(ApplicationArguments args) {
        dbClient = DatabaseClient.create(connFactory);        
        this.databaseUsed = dbProps.getDatabaseUsed();
        log.info("DataInitializer Spring Boot {} database {}", SpringBootVersion.getVersion(), this.databaseUsed);  
        
        log.info("createTables ... ");  
        createTables();
        log.info("Done createTables ");  

        if (dbProps.getSetupMockUserOnly()) {
            dataGenService.seedDataUserOnly();
        } else {
            dataGenService.seedData();
        }
        log.info("Done seedData ");  
        anylyticalService.performAnalytics();
        log.info("Done all DataInitializer Spring Boot {} database {}", SpringBootVersion.getVersion(), this.databaseUsed);  
    }
    
    protected void createTables() {
        boolean tableExists;
        String ddlSql;
        
        for (String table: DDL_TABLES) {
            tableExists = checkTableExists(table);
            if (tableExists) {
                if (dbProps.getTruncateMockData()) {
                    ddlSql = String.format(TRUNC_TABLE, table);
                    if (!ddlSql.isEmpty()) {
                        truncateTableBySql(ddlSql);
                    }
                }
            } else {
                ddlSql = getDdlSql(table);
                if (!ddlSql.isEmpty()) {
                    addTableBySql(ddlSql);
                }
            }
        }        
    }
    
    private void truncateTableBySql(String sql) {
        dbClient.sql(sql).then().block();
    }
    
    private String getDdlSql(String table) {        
        StringBuilder sb = new StringBuilder();
        try{
            InputStream inputStream = getClass().getResourceAsStream(dbProps.getDdlSchemaDir() + table + ".sql");
            sb.append(new String(inputStream.readAllBytes()));
        } catch(IOException ex) {
            log.error("Error getDdlSql table {}", table, ex);
        }
        
        return sb.toString();
    }
    
    private void addTableBySql(String sql) {
        dbClient.sql(sql).then().block(); // Blocking here is okay for initialization
    }
    
    private Boolean checkTableExists(String tableName) {
        return checkPgTableExists(tableName);
    }
    
    private Boolean checkPgTableExists(String tableName) {
        return dbClient.sql("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = $1
            )
            """)
            .bind(0, tableName.toLowerCase())
            .map(row -> row.get(0, Boolean.class))
            .one().defaultIfEmpty(false).block();
    }    

    public static final List<String> DDL_TABLES = List.of("users", "patients", "medications", "physicians", "pharmacies",
        "drugInventories", "prescriptions", "insuranceCompanies", "insuranceProviders", "insurancePlans", 
        "members", "patientMembers", "planPricings", "coverageDetails", "geographicPricings", "claimsData");
}
