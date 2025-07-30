/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto;

import lombok.Data;

@Data
public class LoginResponse {
    String token;
    
    public LoginResponse(String token) {
        this.token = token;
    }
}
