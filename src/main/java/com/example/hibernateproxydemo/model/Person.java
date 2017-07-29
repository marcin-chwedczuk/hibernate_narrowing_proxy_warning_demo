package com.example.hibernateproxydemo.model;

import com.example.hibernateproxydemo.model.houses.House;
import com.example.hibernateproxydemo.model.pets.Pet;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person extends BaseEntity {
    @Column(name = "person_name", nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "house_id")
    private House house;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> pets = new HashSet<>(0);

    protected Person() { }
    public Person(String name, House house) {
        this.name = name;
        this.house = house;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Set<Pet> getPets() {
        return pets;
    }
    public void addPet(Pet pet) {
        pet.setOwner(this);
        pets.add(pet);
    }

    public House getHouse() {
        return house;
    }
    public void setHouse(House house) {
        this.house = house;
    }
}
