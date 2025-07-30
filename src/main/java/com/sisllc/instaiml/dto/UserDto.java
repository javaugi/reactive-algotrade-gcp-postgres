package com.sisllc.instaiml.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.OffsetDateTime;

public record UserDto(
        String name,
        String username,
        String password,
        String roles,
    
        @NotBlank
        String email,
        String phone,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @PositiveOrZero
        int age,

        @NotBlank
        String city,
        
        OffsetDateTime createdDate, 

        OffsetDateTime updatedDate
) {}

