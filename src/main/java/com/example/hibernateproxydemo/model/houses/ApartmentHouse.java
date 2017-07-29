package com.example.hibernateproxydemo.model.houses;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("apartment")
public class ApartmentHouse extends House {
    private int floorNumber;

    protected ApartmentHouse() {}

    public ApartmentHouse(String address, int floorNumber) {
        super(address);
        this.floorNumber = floorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }
    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    @Override
    public String toString() {
        return "ApartmentHouse{" +
                "floorNumber=" + floorNumber +
                "} " + super.toString();
    }
}
