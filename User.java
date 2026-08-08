package com.bobo.app.model;

public class User {

    private String username;
    private String password;
    private String role;
    private String child;

    public User(String username, String password, String role, String child) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.child = child;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getChild() {
        return child;
    }
}