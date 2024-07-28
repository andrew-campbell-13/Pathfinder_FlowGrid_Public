package org.example.DataParsing;

import com.mapbox.geojson.*;
import org.example.MapObjects.DeliveryPoint;
import org.example.MapObjects.Grid;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


// parse the input building and delivery point files
public class MapParser {

    // takes Json file of no-fly-zones and parses them into GeoJson objects
    public static ArrayList<Geometry> getBuildings(String fileName) {
        FeatureCollection buildings = extractFeatureCollection("/Buildings/" + fileName);
        ArrayList<Geometry> noFlyZones = new ArrayList<Geometry>();

        if (buildings == null) {
            return noFlyZones;
        }

        for (Feature f : buildings.features()) {
            noFlyZones.add(f.geometry());
        }
        return noFlyZones;
    }

    //   convert geojson Polygon (repesenting a building) into a list of coordinates of the vertices
    public static ArrayList<ArrayList<Double>> polygonToCoordinates(Polygon polygon) {
        ArrayList<ArrayList<Double>> lngLatList = new ArrayList<ArrayList<Double>>();

        for (Point point : polygon.outer().coordinates()) {
            ArrayList<Double> vertex = new ArrayList<>();

            if (!Grid.validateCoords(point.longitude(), point.latitude())) {
                return lngLatList;
            }
            vertex.add(point.longitude());
            vertex.add(point.latitude());
            lngLatList.add(vertex);
        }

        return lngLatList;
    }

    // create the DeliveryPoint objects from a list of provided coordinates
    public static ArrayList<DeliveryPoint> makeDeliveryPoints(String fileName) {
        ArrayList<DeliveryPoint> deliveryPoints = new ArrayList<DeliveryPoint>();
        try {
            JSONArray jsonArray = new JSONArray(parseFile("/DeliveryPoints/" +fileName));

            // Process the JSON array, extract coordinates and add DeliveryPoint
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");

                if (!Grid.validateCoords(longitude, latitude)) {
                    System.err.println("Point " + i + " is outwith the grid limits and so will be omitted");
                    continue;
                }


                ArrayList<Double> coordinates = new ArrayList<>();
                coordinates.add(longitude);
                coordinates.add(latitude);

                deliveryPoints.add(new DeliveryPoint(coordinates, i));

            }
        } catch (Exception e) {
            System.out.println("delivery point file needs to be json array");
        }

        return deliveryPoints;
    }


    //  extract FeatureCollection from geojson file
    public static FeatureCollection extractFeatureCollection(String fileName) {
        String targetFile;
        try {
            targetFile = parseFile(fileName);
            return FeatureCollection.fromJson(targetFile);
        } catch (Exception e) {
            System.err.println("The file was found");
            System.err.println("An error occurred whilst extracting features from the file");
        }
        return null;
    }

    //parse file into String
    private static String parseFile(String fileName) {
        String fileString = "";
        String currentDirectory = System.getProperty("user.dir");

        try {
            BufferedReader read = new BufferedReader(
                    new FileReader(currentDirectory + fileName));

            String i;
            while ((i = read.readLine()) != null) {
                fileString = fileString + i;
            }
            read.close();
        } catch (IOException e) {
            System.err.println("could not read file: " + currentDirectory  + fileName);
        }

        return fileString;
    }


}
