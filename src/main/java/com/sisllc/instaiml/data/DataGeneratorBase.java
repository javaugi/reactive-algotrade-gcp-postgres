/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;

public class DataGeneratorBase {

    public static final Random rand = new Random();
    public static final Faker FAKER = new Faker();

    public static final List<String> MED_SPECIALTIES = List.of("Allergy and immunology", "Anesthesiology", "Dermatology", "Diagnostic radiology",
        "Emergency medicine", "Family medicine", "Internal medicine", "Medical genetics", "Neurology", "Nuclear medicine", "Obstetrics and gynecology",
        "Ophthalmology", "Pathology", "Pediatrics", "Physical medicine and rehabilitation", "Preventive medicine", "Psychiatry", "Radiation oncology",
        "Surgery", "Urology");

    protected static String generateAgeGroupBracket() {
        StringBuilder sb = new StringBuilder();
        int ndx = FAKER.number().numberBetween(0, 6);
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
        String stateAbbr = FAKER.address().stateAbbr();
        if (StringUtils.isNotEmpty(stateAbbr)) {
            return stateAbbr;
        }
        
        return "MI";
    }
        
    protected static String getZipCodeByStateAbbr(String stateAbbr) {
        String zipCode = FAKER.address().zipCodeByState(stateAbbr);
        if (StringUtils.isNotEmpty(zipCode)) {
            return zipCode;
        }        
        return "48198";
    }    
}
