package org.example;

import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Polygon;
import org.example.DataOutput.MapVisuals;
import org.example.DataParsing.MapParser;
import org.example.MapObjects.DeliveryPoint;
import org.example.MapObjects.Grid;
import org.example.MapObjects.Node;
import org.example.Pathfinding.AStarGridSearch;
import org.example.Pathfinding.SortDeliveryPoints;
import org.example.Pathfinding.Util;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {

        String deliveryPointsFileName = args[5];
        String buildingsFileName = args[6];

        double lat_top = Double.parseDouble(args[0]);
        double lat_bottom = Double.parseDouble(args[1]);
        double lng_left = Double.parseDouble(args[2]);
        double lng_right = Double.parseDouble(args[3]);
        double squareSize = Double.parseDouble(args[4]);

        double[] boundaryLngs = {lng_left, lng_right};
        double[] boundaryLats = {lat_top, lat_bottom};

        boolean showGrid = true; //show the geojson Feature representing grid used for A Star search
        boolean showCrossPoint = false; //show intersection points of building with grid lines, good for debugging
        boolean showBuildingLines = true; // show building lines (geojson polygons)
        boolean showDeliveryPoints = true; // show the delivery points being visited
        boolean showLinePath = true; // show the path of the drone visiting the delivery points
        boolean showBoundary = true; // show the boundary set by the lat lng inputs - can see if grid covers full area
        boolean showAStar = true; // show the grid nodes explored during A* search
        boolean showBlocked = true; // show the grid nodes blocked as they contain a building line

        if (!validateUserInput(lat_bottom, lat_top, lng_left, lng_right, squareSize)) {
            System.out.println("please amend input parameters");
            System.err.println("exiting program");
            return;
        }

        // create grid object once user input is validated
        Grid grid = new Grid(squareSize, lat_top, lat_bottom, lng_left, lng_right);

        //create map class to create visualised pathfinder geojson file
        MapVisuals mapVis = new MapVisuals(grid.getCrossPoints(), squareSize, boundaryLngs, boundaryLats);

        // create delivery points
        ArrayList<DeliveryPoint> deliveryPoints = new ArrayList<>(MapParser.makeDeliveryPoints(deliveryPointsFileName));

        if (!validateDeliveryPoints(deliveryPoints)) {
            return;
        }

        // execute methods to parse buildings
        getBuildings(buildingsFileName, grid);

        // compute best order in which to visit the DPs
        System.out.println("Sorting Started");
        ArrayList<Integer> visitOrder = sortDPVisitOrder(deliveryPoints);
        System.out.println("Sorting Complete");


        // run A* search between all delivery points
        System.out.println("Pathfinding Started");
        ArrayList<Node> droneSquarePath = runPathfinder(deliveryPoints, visitOrder, grid);
        System.out.println("Pathfinding Complete");

        //create geojson visualisation
        System.out.println("Creating GeoJson File");
        mapVis.createGeoJson(grid.getGrid(), buildingsFileName, deliveryPoints, droneSquarePath, showGrid,
                showCrossPoint, showBuildingLines, showDeliveryPoints, showLinePath, showBoundary, showAStar,
                showBlocked);

    }

    //run A* Search to create drone path between delivery points, return the grid nodes in the order they were
    // travelled to
    public static ArrayList<Node> runPathfinder(ArrayList<DeliveryPoint> deliveryPoints, ArrayList<Integer> visitOrder,
                                                Grid grid) {

        ArrayList<Node> droneSquarePath = new ArrayList<>();
        int totalDpVisited = 0;
        for (int i = 0; i < visitOrder.size() - 1; i++) {
            //set target DPs based on the order in which to visit them
            DeliveryPoint startDP = deliveryPoints.get(visitOrder.get(i));
            DeliveryPoint targetDP = deliveryPoints.get(visitOrder.get(i + 1));

            // set the start and target grid nodes based on the coordinates of the starts and target DPs
            Node startNode = grid.coordinateToGridSquare(startDP.getCoordinates().get(0),
                    startDP.getCoordinates().get(1));
            Node targetNode = grid.coordinateToGridSquare(targetDP.getCoordinates().get(0),
                    targetDP.getCoordinates().get(1));

            // run A* on grid nodes, if no path found between dp's, break stop the pathfinder
            List<Node> singlePath = AStarGridSearch.findPath(grid.getGrid(), startNode, targetNode);
            if (singlePath == null) {
                System.err.println(
                        "No path from DP " + visitOrder.get(i) + " to " + visitOrder.get(i + 1) + " can be found");
                break;
            }
            droneSquarePath.addAll(singlePath);
            totalDpVisited = i;
        }
        System.out.println("totalDpVisited: " + totalDpVisited);

        return droneSquarePath;

    }

    public static boolean validateUserInput(double lat_bottom, double lat_top, double lng_left, double lng_right,
                                            double squareSize) {
        if (lat_bottom > lat_top) {
            System.err.println("lat top must be greater than lat bottom");
            return false;
        } else if (lng_left > lng_right) {
            System.err.println("lng right must be greater than lng left");
            return false;
        } else if ((lng_right - lng_left) < squareSize) {
            System.err.println("square size must be smaller than map width");
            return false;
        } else if ((lat_top - lat_bottom) < squareSize) {
            System.err.println("square size must be smaller than map height");
            return false;
        } else if ((lng_right - lng_left) < (squareSize * 6)) {
            System.err.println("grid width must at least 6 squares");
            return false;
        } else if ((lat_top - lat_bottom) < (squareSize * 6)) {
            System.err.println("grid height must at least 6 squares ");
            return false;
        } else {
            return true;
        }

    }

    public static boolean validateDeliveryPoints(ArrayList<DeliveryPoint> deliveryPoints) {
        if (deliveryPoints.size() == 0) {
            System.err.println("no delivery points found in file");
            System.err.println("exiting program");
            return false;
        } else if (deliveryPoints.size() > 22) {
            System.err.println("the maximum number of delivery points is 22");
            System.err.println("exiting program");
            return false;
        }
        return true;
    }

    //sort the order in which the DP's will be visited into the shortest journey
    public static ArrayList<Integer> sortDPVisitOrder(ArrayList<DeliveryPoint> deliveryPoints) {
        double[][] adjacencyMatrix = Util.makeAdjacencyMatrix(deliveryPoints);
        ArrayList<Integer> dpOrder = SortDeliveryPoints.runSorter(adjacencyMatrix);
        return dpOrder;
    }

    // Get buildings(polygons) from geojson file
    public static void getBuildings(String buildingsFileName, Grid grid) {

        ArrayList<Geometry> buildings = MapParser.getBuildings(buildingsFileName);
        if (buildings.size() == 0) {
            System.err.println("No building polygons extracted from buildings file");
            System.err.println("exiting program");
            return;
        }


        // extract the coordinates of each building edge, exclude buildings which are not fully within grid limits
        ArrayList<ArrayList<ArrayList<Double>>> buildingEdgeCoordinates = new ArrayList<>();
        for (int i = 0; i < buildings.size(); i++) {
            Geometry building = buildings.get(i);
            ArrayList<ArrayList<Double>> buildingEdgeCoords = MapParser.polygonToCoordinates((Polygon) building);

            if (buildingEdgeCoords.size() == 0) {
                System.err.println("building " + i + " is not within grid parameters so" + " will be " + "excluded");
                continue;
            }
            buildingEdgeCoordinates.add(MapParser.polygonToCoordinates((Polygon) building));
        }

        // block the grid nodes that the building edge occupies
        for (ArrayList<ArrayList<Double>> buildingEdge : buildingEdgeCoordinates) {
            grid.blockGridSquares(buildingEdge);
        }
    }


}
