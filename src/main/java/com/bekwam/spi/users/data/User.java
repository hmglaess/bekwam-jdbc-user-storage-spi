package com.bekwam.spi.users.data;

import java.util.LinkedHashMap;
import java.util.Map;

public record User(Map<String, String> values) {

    public String username()  { return values.get("username"); }
    public String password()  { return values.get("password"); }
    public String firstName() { return values.get("firstName"); }
    public String lastName()  { return values.get("lastName"); }
    public String email()     { return values.get("email"); }

    /** Alles außer den reservierten Kernfeldern -> Custom-Attribute */
    public Map<String, String> customAttributes() {
        Map<String, String> custom = new LinkedHashMap<>(values);
        custom.remove("username");
        custom.remove("password");
        custom.remove("firstName");
        custom.remove("lastName");
        custom.remove("email");
        return custom;
    }

    @Override
    public String toString() {
        return "User{username='" + username() + "', password not null?='" + (password()!=null) +
               "', firstName='" + firstName() + "', lastName='" + lastName() +
               "', email='" + email() + "', customAttributes=" + customAttributes() + '}';
    }
}
