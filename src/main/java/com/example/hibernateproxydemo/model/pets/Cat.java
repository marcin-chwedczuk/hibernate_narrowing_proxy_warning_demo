package com.example.hibernateproxydemo.model.pets;

import com.example.hibernateproxydemo.model.pets.Pet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("cat")
public class Cat extends Pet {
    protected Cat() { }

    public Cat(String name) {
        super(name);
    }

    @Override
    public String makeNoise() {
        return "Meow, meeeeeow!";
    }
}
