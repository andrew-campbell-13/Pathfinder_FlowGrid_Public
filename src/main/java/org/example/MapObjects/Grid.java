package org.example.MapObjects;

import java.util.ArrayList;

public class Grid {

    // set bounderies of grid
    private static double lat_top;
    private static double lat_bottom;
    private static double lng_left;
    private static double lng_right;
    private final Node[][] grid;
    private final double squareSize;
    private final double bump = 0.000001; // increment lat/lng enough for point to be in next grid square
    private final ArrayList<ArrayList<Double>> crossPoints;  // points where building polygon lines cross grid lines


    public Grid(double squareSize, double lat1, double lat2, double lng1, double lng2) {
        this.squareSize = squareSize;
        this.crossPoints = new ArrayList<ArrayList<Double>>();
        lat_top = lat1;
        lat_bottom = lat2;
        lng_left = lng1;
        lng_right = lng2;
        this.grid = createGrid();
    }

    public static boolean validateCoords(double x, double y) {
        return !(x < lng_left || x >= lng_right || y <= lat_bottom || y >= lat_top);
    }

    //  divide map into square areas of size n
    //  currently assuming the map does not cross the line of lat=0 or lng=0
    public Node[][] createGrid() {

        int numSquaresX = (int) Math.round((lng_right - lng_left) / squareSize); // calculate number of squares on X
        // axis
        int numSquaresY = (int) Math.round((lat_top - lat_bottom) / squareSize); // calculate number of squares on Y
        // axis

//      if grid does not perfectly fit in boundaries - allow grid to be slightly larger than boundary
//        if((squareSize%numSquaresX)>0){
//            numSquaresX++;
//        }
//        if((squareSize%numSquaresY)>0){
//            numSquaresY++;
//        }

        Node[][] map = new Node[numSquaresY][numSquaresX];

        for (int i = 0; i < numSquaresY; i++) {
            for (int j = 0; j < numSquaresX; j++) {
                double[] topLeftCoord = new double[2];

                topLeftCoord[0] = roundValue(lng_left + (j * squareSize));
                topLeftCoord[1] = roundValue(lat_bottom + (i * squareSize));

                map[i][j] = new Node(topLeftCoord, j, i);
            }
        }


        return map;
    }

    public Node coordinateToGridSquare(double x, double y) {

        int gridX = Math.abs((int) roundValue((x - lng_left) / squareSize));
        int gridY = Math.abs((int) roundValue((y - lat_bottom) / squareSize));

        return grid[gridY][gridX];
    }

    public void blockGridSquares(ArrayList<ArrayList<Double>> vertices) {
        ArrayList<ArrayList<Double>> crossPoints = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < vertices.size() - 1; i++) {

            // calculate function of line between the two vertices
            ArrayList<Double> v1 = vertices.get(i);
            ArrayList<Double> v2 = vertices.get(i + 1);

            double x1 = v1.get(0);
            double y1 = v1.get(1);
            double x2 = v2.get(0);
            double y2 = v2.get(1);

//          each time the building line crosses a grid line, mark the grid square as blocked
            double gradient = (y1 - y2) / (x1 - x2);
            double yIntercept = y1 - gradient * x1;
            double x = x1 - (x1 % squareSize);
            double y = y1 - (y1 % squareSize);

            // mark blocked grid squares when building line crosses latitude grid lines
            if (x1 < x2) {
                // for negative lngs
                if (x > 0) {
                    x += squareSize;
                }

                findLatCrosses(gradient, y1, x, x2, yIntercept, true);
            } else {
                // for negative lngs
                if (x < 0) {
                    x -= squareSize;
                }
                findLatCrosses(gradient, y1, x, x2, yIntercept, false);
            }

            // mark blocked grid squares when building line crosses longitude grid lines
            if (y1 < y2) {
                if (y > 0) {
                    y += squareSize;
                }
                findLngCrosses(gradient, x1, y, y2, yIntercept, true);
            } else {
                if (y < 0) {
                    y -= squareSize;
                }
                findLngCrosses(gradient, x1, y, y2, yIntercept, false);
            }

        }

    }

    // calculate points where a building line intersects a grid lng line
    public void findLngCrosses(double gradient, double x, double y, double y2, double yIntercept, boolean x1Smaller) {
        while (x1Smaller ? y < y2 : y > y2) {


            if (!Double.isInfinite(gradient)) {
                x = (y - yIntercept) / gradient;
            }

            addCrossPoint(x, y);
            Node cNode = coordinateToGridSquare(x, x1Smaller ? y + bump : y - bump);
            cNode.setBlocked(true);

            y = x1Smaller ? y + squareSize : y - squareSize;
        }
    }

    // calculate points where a building line intersects a grid lat line
    public void findLatCrosses(double gradient, double y, double x, double x2, double yIntercept, boolean y1Smaller) {
        while (y1Smaller ? x < x2 : x > x2) {

            if (gradient != 0) {
                y = (gradient * x) + yIntercept;
            }


            addCrossPoint(x, y);
            Node cNode = coordinateToGridSquare(y1Smaller ? x + bump : x - bump, y);
            cNode.setBlocked(true);
            x = y1Smaller ? x + squareSize : x - squareSize;
        }
    }

    public void addCrossPoint(Double lng, Double lat) {
        ArrayList<Double> crossPoint = new ArrayList<Double>();
        crossPoint.add(lng);
        crossPoint.add(lat);
        crossPoints.add(crossPoint);
    }

    public Node[][] getGrid() {
        return grid;
    }

    public ArrayList<ArrayList<Double>> getCrossPoints() {
        return crossPoints;
    }

    private double roundValue(double val) {
        return (double) Math.round(val * 1e6) / 1e6;

    }


}
