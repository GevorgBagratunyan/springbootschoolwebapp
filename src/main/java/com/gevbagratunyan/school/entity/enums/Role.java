package com.gevbagratunyan.school.entity.enums;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER"),
    MANAGER("MANAGER");

    String role;

    Role(String role) {
        this.role=role.toUpperCase();
    }

    @Override
    public String toString(){
        return role;
    }

    public static Role stringToEnum(String role){
        Role r;
        switch(role.toUpperCase()){
            case "ADMIN":
                r=Role.ADMIN;
                break;
            case "USER":
            r=Role.USER;
                break;
            case "MANAGER":
            r=Role.MANAGER;
                break;
            default: throw new IllegalArgumentException("invalid role type");
        }
        return r;
    }
}
