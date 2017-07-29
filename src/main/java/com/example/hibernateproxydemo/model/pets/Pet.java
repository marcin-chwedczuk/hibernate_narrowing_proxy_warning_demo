package com.example.hibernateproxydemo.model.pets;

import com.example.hibernateproxydemo.model.BaseEntity;
import com.example.hibernateproxydemo.model.Person;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "animal_type")
public abstract class Pet extends BaseEntity {
    @Column
    private String name;

    @JoinColumn(name = "owner_id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Person owner;

    public abstract String makeNoise();

    @Deprecated // for JPA
    protected Pet() { }

    protected Pet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
