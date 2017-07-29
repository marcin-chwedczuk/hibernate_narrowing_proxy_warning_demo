package com.example.hibernateproxydemo.model.houses;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("single-family")
public class SingleFamilyDetachedHouse extends House {
    private int roomNumber;
    private boolean hasVeranda;

    protected SingleFamilyDetachedHouse() { }

    public SingleFamilyDetachedHouse(String address, int roomNumber, boolean hasVeranda) {
        super(address);
        this.roomNumber = roomNumber;
        this.hasVeranda = hasVeranda;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isHasVeranda() {
        return hasVeranda;
    }

    public void setHasVeranda(boolean hasVeranda) {
        this.hasVeranda = hasVeranda;
    }

    @Override
    public String toString() {
        return "SingleFamilyDetachedHouse{" +
                "roomNumber=" + roomNumber +
                ", hasVeranda=" + hasVeranda +
                "} " + super.toString();
    }
}
