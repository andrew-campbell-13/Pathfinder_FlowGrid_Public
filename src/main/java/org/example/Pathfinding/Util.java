package org.example.Pathfinding;

import org.example.MapObjects.DeliveryPoint;

import java.util.ArrayList;

public class Util {
    public static DeliveryPoint findClosestSensor(ArrayList<DeliveryPoint> sensors, ArrayList<Double> coords, double mapLength) {

        double closestDistance = mapLength;
        DeliveryPoint closestSensor = sensors.get(0);

        for (int i = 0; i < sensors.size() - 1; i++) {
            double distance = calcEuclideanDistance(sensors.get(i).getCoordinates(), coords);
            if (distance < closestDistance && distance != 0) {
                closestDistance = distance;
                closestSensor = sensors.get(i);
            }
        }
        return closestSensor;
    }


    public static double calcEuclideanDistance(ArrayList<Double> startCoords, ArrayList<Double> targetCoords) {

        double cX = startCoords.get(0);
        double cY = startCoords.get(1);
        double tX = targetCoords.get(0);
        double tY = targetCoords.get(1);

        return Math.sqrt(Math.pow((tY - cY), 2) + Math.pow((tX - cX), 2));

    }

    public static double[][] makeAdjacencyMatrix(ArrayList<DeliveryPoint> mapSensors) {
        // copies the arrayList of mapSensors so as not to alter the original ArrayList
        // when removing sensors
        var mapS = new ArrayList<DeliveryPoint>(mapSensors);

        int numSensors = mapSensors.size();


        double[][] adjacencyMatrix = new double[numSensors + 1][numSensors + 1];


        for (int i = 0; i < numSensors; i++) {
            for (int j = 0; j < numSensors; j++) {
                adjacencyMatrix[i][j] = calcEuclideanDistance(mapS.get(i).getCoordinates(), mapS.get(j).getCoordinates());
            }
        }

        return adjacencyMatrix;
    }


}
