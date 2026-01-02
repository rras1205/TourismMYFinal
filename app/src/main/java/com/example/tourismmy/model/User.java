package com.example.tourismmy.model;

public class User {
    private String uid;
    private String name;
    private String email;
    private String role; // "tourist", "business", or "admin"

    // Empty constructor required for Firebase
    public User() { }

    public User(String uid, String name, String email, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}