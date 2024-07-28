package org.example.MapObjects;

import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Polygon;
import org.example.DataParsing.MapParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GridTestAustralia {

    final int[] TRUE_BLOCKED_XS = {17, 18, 19, 20, 12, 13, 14, 15, 16, 17, 20, 7, 8, 9, 10, 11, 12, 20, 5, 6, 7, 20,
            5, 20, 5, 18, 19, 20, 5, 14, 15, 16, 17, 18, 5, 10, 11, 12, 13, 14, 5, 7, 8, 9, 10, 23, 24, 25, 26, 5, 6,
            7, 22, 23, 26, 27, 28, 29, 22, 29, 30, 31, 32, 33, 13, 14, 15, 21, 22, 33, 34, 12, 13, 15, 16, 17, 18, 19
            , 21, 22, 23, 24, 33, 34, 12, 19, 24, 25, 26, 27, 33, 11, 12, 18, 19, 27, 28, 29, 30, 31, 32, 33, 11, 12,
            13, 18, 31, 32, 13, 14, 15, 16, 18, 16, 17, 18, 26, 27, 28, 25, 26, 27, 28, 25, 26, 27, 28, 25, 27, 28,
            25, 26, 27, 28, 29, 30, 31, 28, 31, 32, 33, 27, 28, 32, 27, 31, 32, 27, 28, 29, 30, 31, 30, 31, 38, 39,
            40, 41, 42, 38, 42, 12, 13, 14, 38, 41, 42, 12, 13, 14, 15, 16, 17, 18, 37, 38, 41, 42, 15, 16, 17, 18,
            19, 37, 38, 39, 42, 43, 18, 39, 40, 41, 42, 43};
    final int[] TRUE_BLOCKED_YS = {1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6
            , 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11,
            11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14,
            14, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16, 17, 17, 17,
            17, 17, 18, 18, 18, 22, 22, 22, 23, 23, 23, 23, 24, 24, 24, 24, 25, 25, 25, 26, 26, 26, 28, 28, 28, 28,
            29, 29, 29, 29, 30, 30, 30, 31, 31, 31, 32, 32, 32, 32, 32, 33, 33, 36, 36, 36, 36, 36, 37, 37, 38, 38,
            38, 38, 38, 38, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 41,
            41, 41, 41, 41, 41};
    final int TRUE_NUM_BLOCKED_SQUARES = 194;

    Grid grid;
    double lat_top = -37.8070;
    double lat_bottom = -37.8170;
    double lng_left = 144.9590;
    double lng_right = 144.9690;
    double squareSize = 0.0002;
    int TRUE_NUM_SQUARES_X = 50;
    int TRUE_NUM_SQUARES_Y = 50;


    // test correct grid squares blocked

    @BeforeAll
    public void setUp() {

        grid = new Grid(squareSize, lat_top, lat_bottom, lng_left, lng_right);

        System.out.println("running before");

        //Get buildings(polygons) from geojson file
        ArrayList<Geometry> buildings = MapParser.getBuildings("buildings_Australia.geojson");
        ArrayList<ArrayList<ArrayList<Double>>> buildingEdgeCoordinates = new ArrayList<ArrayList<ArrayList<Double>>>();

        // extract the coordinates of each building edge
        for (Geometry building : buildings) {
            buildingEdgeCoordinates.add(MapParser.polygonToCoordinates((Polygon) building));
        }

        // block the grid nodes that the building edge occupies
        for (ArrayList<ArrayList<Double>> buildingEdge : buildingEdgeCoordinates) {
            grid.blockGridSquares(buildingEdge);
        }

    }


    @Test
    void testGridWidth() {
        assertEquals(TRUE_NUM_SQUARES_X, grid.getGrid()[0].length);
    }

    @Test
    void testGridHeight() {
        assertEquals(TRUE_NUM_SQUARES_Y, grid.getGrid().length);
    }

    @Test
    void testGridTopRightCoord() {
        Node topRightSquare = grid.getGrid()[TRUE_NUM_SQUARES_Y - 1][TRUE_NUM_SQUARES_X - 1];
        double[] coords = topRightSquare.getBottomLeftCoord();

        assertTrue(coords[0] < lng_right);
        assertTrue(coords[0] >= (lng_right - squareSize));

        assertTrue(coords[1] < lat_top);
        assertTrue(coords[1] >= (lat_top - squareSize));
    }

    @Test
    void testCoordinateToGridSquare() {
        double bump = squareSize / 2;

        Node test00 = grid.coordinateToGridSquare(lng_left, lat_bottom);
        Node testNM = grid.coordinateToGridSquare(lng_right-0.00001, lat_top-0.00001);
        Node test23 = grid.coordinateToGridSquare(lng_left + (3 * squareSize) - bump,
                lat_bottom + (4 * squareSize) - bump);
        Node test34 = grid.coordinateToGridSquare(lng_left + (3 * squareSize) + bump,
                lat_bottom + (4 * squareSize) + bump);


        Node true00 = grid.getGrid()[0][0];
        Node trueNM = grid.getGrid()[TRUE_NUM_SQUARES_Y - 1][TRUE_NUM_SQUARES_X - 1];
        Node true23 = grid.getGrid()[3][2];
        Node true34 = grid.getGrid()[4][3];

        assertTrue((true00.getX() == test00.getX()) && (true00.getY() == test00.getY()));
        assertTrue((trueNM.getX() == testNM.getX()) && (trueNM.getY() == testNM.getY()));
        assertTrue((true23.getX() == test23.getX()) && (true23.getY() == test23.getY()));
        assertTrue((true34.getX() == test34.getX()) && (true34.getY() == test34.getY()));
    }

    @Test
    void testBlockGridSquares() {
        for (int i = 0; i < TRUE_BLOCKED_XS.length; i++) {
            Node square = grid.getGrid()[TRUE_BLOCKED_YS[i]][TRUE_BLOCKED_XS[i]];
            assertTrue(square.isBlocked());
        }
    }

    @Test
    void testNumGridSquaresBlocked() {
        int count = 0;
        for (int i = 0; i < TRUE_NUM_SQUARES_Y - 1; i++) {
            for (int j = 0; j < TRUE_NUM_SQUARES_X - 1; j++) {
                if (grid.getGrid()[i][j].isBlocked()) {
                    count++;
                }
            }
        }

        assertTrue(count == TRUE_NUM_BLOCKED_SQUARES);
    }





}