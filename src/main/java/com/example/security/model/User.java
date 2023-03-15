package com.example.security.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;    // ROLE_USER, ROLE_ADMIN, ROLE_MANAGER
    private String provider;    // google, facebook, naver
    private String providerId;  // google 회원 Id(sub)
    @CreationTimestamp
    private Timestamp createDate;

}
