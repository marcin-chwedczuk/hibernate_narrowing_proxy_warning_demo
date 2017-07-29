package com.example.hibernateproxydemo.model.pets;

import com.example.hibernateproxydemo.model.pets.Pet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("dog")
public class Dog extends Pet {
    protected Dog() { }
    public Dog(String name) {
        super(name);
    }

    @Override
    public String makeNoise() {
        return "Woof, woof";
    }
}
