package com.mysociety.authentication.enums;

public enum Role {

    RESIDENT("resident"),
    ADMIN("admin"),
    GUARD("guard");

    private final String displayName;
    Role(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return this.displayName;
    }
}
