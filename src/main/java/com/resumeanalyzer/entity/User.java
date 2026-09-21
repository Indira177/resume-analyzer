package com.resumeanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public User() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public User(
            String username,
            String email,
            String password) {

        this.username = username;
        this.email = email;
        this.password = password;
    }

    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }

    // =========================================================
    // SET ID
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    // =========================================================
    // GET USERNAME
    // =========================================================

    public String getUsername() {
        return username;
    }

    // =========================================================
    // SET USERNAME
    // =========================================================

    public void setUsername(String username) {
        this.username = username;
    }

    // =========================================================
    // GET EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }

    // =========================================================
    // SET EMAIL
    // =========================================================

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================================
    // GET PASSWORD
    // =========================================================

    public String getPassword() {
        return password;
    }

    // =========================================================
    // SET PASSWORD
    // =========================================================

    public void setPassword(String password) {
        this.password = password;
    }
}