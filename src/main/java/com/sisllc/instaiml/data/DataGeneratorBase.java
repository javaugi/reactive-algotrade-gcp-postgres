/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;

public class DataGeneratorBase {

    public static final Random rand = new Random();
    public static final Faker JAVA_FAKER = new Faker();
    public static final net.datafaker.Faker NET_FAKER = new net.datafaker.Faker();

    public static final List<String> MED_SPECIALTIES = List.of("Allergy and immunology", "Anesthesiology", "Dermatology", "Diagnostic radiology",
        "Emergency medicine", "Family medicine", "Internal medicine", "Medical genetics", "Neurology", "Nuclear medicine", "Obstetrics and gynecology",
        "Ophthalmology", "Pathology", "Pediatrics", "Physical medicine and rehabilitation", "Preventive medicine", "Psychiatry", "Radiation oncology",
        "Surgery", "Urology");

    protected static String generateAgeGroupBracket() {
        StringBuilder sb = new StringBuilder();
        int ndx = JAVA_FAKER.number().numberBetween(0, 6);
        if (ndx >= 6) {
            sb.append("60+");
        } else {
            sb.append(ndx);
            if (ndx > 0) {
                sb.append(0);
            }
            sb.append("-");

            if (ndx > 0) {
                sb.append(ndx);
            }
            sb.append(9);
        }
        return sb.toString();
    }
   
    protected static String getStateAbbr() {
        String stateAbbr = NET_FAKER.address().stateAbbr();
        if (StringUtils.isNotEmpty(stateAbbr)) {
            return stateAbbr;
        }
        
        return "MI";
    }
        
    protected static String getZipCodeByStateAbbr(String stateAbbr) {
        String zipCode = NET_FAKER.address().zipCodeByState(stateAbbr);
        if (StringUtils.isNotEmpty(zipCode)) {
            return zipCode;
        }        
        return "48198";
    }    
}
