package com.example.hibernateproxydemo.model.legacysystem;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("EXTENDED")
public class ExtendedUser extends LegacyUser {
    @Column
    private String userPreference3;

    public String getUserPreference3() {
        return userPreference3;
    }

    public void setUserPreference3(String userPreference3) {
        this.userPreference3 = userPreference3;
    }
}
