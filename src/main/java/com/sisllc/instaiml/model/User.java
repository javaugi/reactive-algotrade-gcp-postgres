package com.sisllc.instaiml.model;

import org.springframework.data.annotation.Id;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private String id;

    private String name;
    private String username;
    private String password;
    private String roles;

    private String email;
    private String phone;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private int age;

    private String city;

    @CreatedDate
    @Column("created_date")
    private OffsetDateTime createdDate;   

    @LastModifiedDate
    @Column("updated_date")
    private OffsetDateTime updatedDate;    
}
