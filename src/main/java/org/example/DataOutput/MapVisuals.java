package org.example.DataOutput;

import com.mapbox.geojson.*;
import org.example.DataParsing.MapParser;
import org.example.MapObjects.DeliveryPoint;
import org.example.MapObjects.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// create geojson file conditionally showing the grid, buildings and path
public class MapVisuals {

    private final double squareSize;
    private final double[] boundaryLngs;
    private final double[] boundaryLats;
    public List<Feature> crossPointFeatures;

    public MapVisuals(ArrayList<ArrayList<Double>> crossPoints, double squareSize, double[] boundaryLngs,
                      double[] boundaryLats) {
        this.crossPointFeatures = new ArrayList<>(createPointFeatures(crossPoints));
        this.squareSize = squareSize;
        this.boundaryLngs = boundaryLngs;
        this.boundaryLats = boundaryLats;
    }

    // create visualised grid
    public List<Feature> createAreaSquareVisual(Node[][] map, boolean showAStar, boolean showBlocked) {
        List<Feature> squares = new ArrayList<Feature>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {

                double lng = map[i][j].getBottomLeftCoord()[0];
                double lat = map[i][j].getBottomLeftCoord()[1];

                Feature squareFeature = createGridSquareFeature(lng, lat);

                //style grid square border
                squareFeature.addStringProperty("stroke-width", "0.002");
                squareFeature.addStringProperty("stroke-opacity", "0");

                //add x and y grid coordinates to geojson polygon for reference
                squareFeature.addStringProperty("x", Integer.toString(j));
                squareFeature.addStringProperty("y", Integer.toString(i));

                //conditionally visualise the grid squares A Star explores
                if (showAStar) {
                    if (map[i][j].isExplored()) {
                        squareFeature.addStringProperty("fill", "#ADD8E6");
                        squareFeature.addStringProperty("fill-opacity", "0.2");
                    }

                    if (map[i][j].isPathNode()) {
                        squareFeature.addStringProperty("fill", "#32CD32");
                        squareFeature.addStringProperty("fill-opacity", "0.2");
                    }

                }

                //conditionally visualise the grid squares blocked - for buildings
                if (showBlocked) {
                    if (map[i][j].isBlocked()) {
                        squareFeature.addStringProperty("fill", "#ffffff");
                        squareFeature.addStringProperty("fill-opacity", "0.5");
                    }
                }
                squares.add(squareFeature);
            }
        }


        return squares;
    }

    // create the geojson square representation of a grid node
    private Feature createGridSquareFeature(double lng, double lat) {
        List<List<Point>> verticesWrapper = new ArrayList<>();
        List<Point> vertices = new ArrayList<Point>();

        // create vertex Points of grid square, note - first point is added twice to close polygon
        vertices.add(Point.fromLngLat(lng, lat));
        vertices.add(Point.fromLngLat(lng, lat + squareSize));
        vertices.add(Point.fromLngLat(lng + squareSize, lat + squareSize));
        vertices.add(Point.fromLngLat(lng + squareSize, lat));
        vertices.add(Point.fromLngLat(lng, lat));

        verticesWrapper.add(vertices);

        // create geojson feature visualisation of map square
        Polygon squarePolygon = Polygon.fromLngLats(verticesWrapper);
        return Feature.fromGeometry(squarePolygon);
    }

    // create the geojson linePath representing the flightpath of the drone
    public Feature createLinePath(ArrayList<Node> droneSquarePath) {
        ArrayList<ArrayList<Double>> dronePath = new ArrayList<ArrayList<Double>>();

        //get coordinates of the centre of visited grid squares
        for (Node n : droneSquarePath) {
            dronePath.add(getCentreCoordsOfGridNode(n));
        }

        //create geojson Points from coords of each drone move
        ArrayList<Point> points = pointsFromCoords(dronePath);

        //create geojson multiLinePath path linking each of these points
        Feature dronePathMLS = multiLineStringFromPoints(points, false);

        return dronePathMLS;
    }

    // return the centre point of a grid square
    public ArrayList<Double> getCentreCoordsOfGridNode(Node square) {
        double[] topLeftCoord = square.getBottomLeftCoord();

        ArrayList<Double> centreCoord = new ArrayList<>();
        centreCoord.add(topLeftCoord[0] + (squareSize / 2));
        centreCoord.add(topLeftCoord[1] + (squareSize / 2));

        return centreCoord;
    }

    // create geojson Point from longitude and latitude
    public Feature createPoint(double lng, double lat) {
        Point point = Point.fromLngLat(lng, lat);
        return Feature.fromGeometry(point);
    }

    // convert a list of coordinates into a list of point Features
    public List<Feature> createPointFeatures(ArrayList<ArrayList<Double>> coords) {
        List<Feature> points = new ArrayList<Feature>();

        for (ArrayList<Double> coord : coords) {
            points.add(createPoint(coord.get(0), coord.get(1)));
        }
        return points;
    }

    // create List of Features representing the Depivery Points
    public List<Feature> createDeliveryPointFeatures(ArrayList<DeliveryPoint> deliveryPoints) {
        List<Feature> deliveryPointFeatures = new ArrayList<Feature>();

        for (DeliveryPoint dp : deliveryPoints) {
            Feature dpFeature = createPoint(dp.getCoordinates().get(0), dp.getCoordinates().get(1));
            dpFeature.addStringProperty("id", Integer.toString(dp.getId()));
            deliveryPointFeatures.add(dpFeature);
        }
        return deliveryPointFeatures;
    }


    // create the geojson visualisation of the map boundary
    public Feature createBoundaryLines(double[] lngs, double[] lats) {
        ArrayList<ArrayList<Double>> boundaryVertexCoords = new ArrayList<>();

        //order in which the lng&lats should be accessed in order to create square from points
        int[] lngIndex = {0, 1, 1, 0};
        int[] latIndex = {0, 0, 1, 1};

        for (int i = 0; i < 4; i++) {
            double lng = lngs[lngIndex[i]];
            double lat = lats[latIndex[i]];

            ArrayList<Double> vertex = new ArrayList<>();
            vertex.add(lng);
            vertex.add(lat);
            boundaryVertexCoords.add(vertex);
        }

        ArrayList<Point> vertexPoints = pointsFromCoords(boundaryVertexCoords);
        Feature boundaryFeature = multiLineStringFromPoints(vertexPoints, true);

        return boundaryFeature;
    }

    // create list of geojson points from a list of coordinates
    public ArrayList<Point> pointsFromCoords(ArrayList<ArrayList<Double>> coords) {
        ArrayList<Point> points = new ArrayList<>();

        for (ArrayList<Double> c : coords) {
            double lng = c.get(0);
            double lat = c.get(1);
            Point point = Point.fromLngLat(lng, lat);
            points.add(point);
        }
        return points;
    }

    public Feature multiLineStringFromPoints(ArrayList<Point> points, boolean cycle) {
        List<List<Point>> lineList = new ArrayList<List<Point>>();

        // creating each line requires the geojson endpoints of each line
        for (int i = 0; i < points.size() - 1; i++) {
            List<Point> singleLine = new ArrayList<Point>();
            singleLine.add(points.get(i));
            singleLine.add(points.get(i + 1));
            lineList.add(singleLine);
        }

        // if the end point should link to the start point, create the final line
        if (cycle) {
            List<Point> singleLine = new ArrayList<Point>();
            singleLine.add(points.get(points.size() - 1));
            singleLine.add(points.get(0));
            lineList.add(singleLine);
        }

        // create required geojson feature from line list and add to list
        MultiLineString multiLineString = MultiLineString.fromLngLats(lineList);
        Feature mlsFeature = Feature.fromGeometry(multiLineString);


        return mlsFeature;
    }


    // create geojson output file
    public void createGeoJson(Node[][] map, String buildingFileName, ArrayList<DeliveryPoint> deliveryPoints,
                              ArrayList<Node> droneSquarePath, boolean showGrid, boolean showCrossPoints,
                              boolean showBuildingLines, boolean showDeliveryPoints, boolean showLinePath,
                              boolean showBoundary, boolean showAStar, boolean showBlocked) {
        ArrayList<Feature> mapFeatures = new ArrayList<>();
        var mapFeatureCollection = FeatureCollection.fromFeatures(mapFeatures);

        if (showGrid) {
            mapFeatures.addAll(createAreaSquareVisual(map, showAStar, showBlocked));
        }

        if (showCrossPoints) {
            mapFeatures.addAll(crossPointFeatures);
        }

        if (showBuildingLines) {
            FeatureCollection buildingFeatureCollection = MapParser.extractFeatureCollection("/Buildings/" + buildingFileName);
            mapFeatures.addAll(buildingFeatureCollection.features());
        }

        if (showDeliveryPoints) {
            mapFeatures.addAll(createDeliveryPointFeatures(deliveryPoints));
        }

        if (showLinePath) {
            mapFeatures.add(createLinePath(droneSquarePath));
        }

        if (showBoundary) {
            mapFeatures.add(createBoundaryLines(boundaryLngs, boundaryLats));
        }


        try (FileWriter fileWriter = new FileWriter("drone_path.geojson")) {
            fileWriter.write(mapFeatureCollection.toJson());
        } catch (IOException e) {
            System.out.println("Could not create geojson file");
        }

    }
}
