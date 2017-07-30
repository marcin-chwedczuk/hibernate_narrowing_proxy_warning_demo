package com.example.hibernateproxydemo.model.legacysystem;

import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.*;

@Entity
@Table(name = "extensible_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("NOT_USED")
public class LegacyUser {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String userPreference1;

    @Column
    private String userPreference2;

    public LegacyUser() { }

    /* Notice that this class uses default hashCode()/equals() implementation. */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserPreference1() {
        return userPreference1;
    }

    public void setUserPreference1(String userPreference1) {
        this.userPreference1 = userPreference1;
    }

    public String getUserPreference2() {
        return userPreference2;
    }

    public void setUserPreference2(String userPreference2) {
        this.userPreference2 = userPreference2;
    }
}
