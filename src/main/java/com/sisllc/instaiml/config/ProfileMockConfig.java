/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import java.io.Serializable;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Profiles;

@Profile(ProfileMockConfig.MOCK_PROFILE)
@Configuration
public class ProfileMockConfig implements Serializable {
    private static final long serialVersionUID = 35238717363L;
    public static final String MOCK_PROFILE = "mock";
    public static final Profiles MOCK_PROFILES = Profiles.of("mock", "default", "DEFAULT");
}
