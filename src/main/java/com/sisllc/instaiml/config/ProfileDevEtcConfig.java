/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import java.io.Serializable;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Profiles;

@Configuration
@Profile(ProfileDevEtcConfig.DEV_PROFILE)
public class ProfileDevEtcConfig implements Serializable {
    private static final long serialVersionUID = 321357244048L;
    protected static final String DEV_PROFILE = "dev";
    public static final Profiles DEV_PROFILES = Profiles.of("dev", "test", "qa", "postgres", "staging", "snapshot");
    
}
