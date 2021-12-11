package com.instamic.authservice.model;

public enum Role {

    USER("USER"), ADMIN("ADMIN"), SERVICE("SERVICE");


    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
