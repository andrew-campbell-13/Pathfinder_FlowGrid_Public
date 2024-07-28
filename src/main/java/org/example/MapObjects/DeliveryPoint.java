package org.example.MapObjects;

import java.util.ArrayList;

public class DeliveryPoint {
    private int id;
    private ArrayList<Double> coordinates;

    public DeliveryPoint(ArrayList<Double> coordinates, int id){
        this.coordinates = coordinates;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Double> getCoordinates(){
        return this.coordinates;
    }

    public int getId() {
        return this.id;
    }



}
