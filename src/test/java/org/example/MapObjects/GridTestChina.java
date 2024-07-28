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
class GridTestChina {

    final int[] TRUE_BLOCKED_XS = {31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 25, 26, 27, 28, 29, 30, 31, 42, 43
            , 26, 43, 44, 26, 27, 44, 45, 27, 28, 45, 46, 28, 29, 46, 47, 29, 30, 47, 48, 30, 48, 49, 30, 31, 32, 33,
            34, 35, 49, 50, 35, 36, 37, 38, 39, 40, 41, 42, 43, 50, 51, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 50,
            51, 52, 53, 115, 116, 117, 118, 112, 113, 114, 115, 118, 119, 110, 111, 112, 119, 120, 107, 108, 109, 110
            , 120, 121, 105, 106, 107, 121, 122, 103, 104, 105, 122, 123, 100, 101, 102, 103, 123, 124, 100, 124, 125
            , 126, 100, 101, 126, 127, 101, 102, 127, 128, 102, 103, 128, 129, 103, 104, 128, 129, 104, 105, 125, 126
            , 127, 128, 105, 106, 123, 124, 125, 106, 107, 120, 121, 122, 123, 107, 108, 118, 119, 120, 108, 109, 115
            , 116, 117, 118, 109, 110, 113, 114, 115, 110, 111, 112, 113, 16, 17, 15, 16, 17, 18, 19, 14, 15, 19, 20,
            21, 12, 13, 14, 21, 22, 11, 12, 22, 23, 24, 10, 11, 24, 25, 26, 9, 10, 26, 27, 28, 8, 9, 28, 29, 7, 8, 29
            , 30, 31, 6, 7, 31, 32, 33, 4, 5, 6, 33, 34, 35, 4, 5, 35, 5, 6, 7, 35, 36, 7, 8, 9, 10, 36, 10, 11, 12,
            36, 12, 13, 14, 15, 36, 37, 15, 16, 17, 37, 17, 18, 19, 20, 37, 20, 21, 22, 37, 22, 23, 24, 25, 37, 38,
            25, 26, 27, 38, 27, 28, 29, 30, 38, 30, 31, 32, 33, 38, 39, 33, 34, 35, 39, 35, 36, 37, 38, 39, 38, 39, 40};


    final int[] TRUE_BLOCKED_YS = {23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24
            , 25, 25, 25, 26, 26, 26, 26, 27, 27, 27, 27, 28, 28, 28, 28, 29, 29, 29, 29, 30, 30, 30, 31, 31, 31, 31,
            31, 31, 31, 31, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 34,
            34, 34, 34, 46, 46, 46, 46, 47, 47, 47, 47, 47, 47, 48, 48, 48, 48, 48, 49, 49, 49, 49, 49, 49, 50, 50,
            50, 50, 50, 51, 51, 51, 51, 51, 52, 52, 52, 52, 52, 52, 53, 53, 53, 53, 54, 54, 54, 54, 55, 55, 55, 55,
            56, 56, 56, 56, 57, 57, 57, 57, 58, 58, 58, 58, 58, 58, 59, 59, 59, 59, 59, 60, 60, 60, 60, 60, 60, 61,
            61, 61, 61, 61, 62, 62, 62, 62, 62, 62, 63, 63, 63, 63, 63, 64, 64, 64, 64, 99, 99, 100, 100, 100, 100,
            100, 101, 101, 101, 101, 101, 102, 102, 102, 102, 102, 103, 103, 103, 103, 103, 104, 104, 104, 104, 104,
            105, 105, 105, 105, 105, 106, 106, 106, 106, 107, 107, 107, 107, 107, 108, 108, 108, 108, 108, 109, 109,
            109, 109, 109, 109, 110, 110, 110, 111, 111, 111, 111, 111, 112, 112, 112, 112, 112, 113, 113, 113, 113,
            114, 114, 114, 114, 114, 114, 115, 115, 115, 115, 116, 116, 116, 116, 116, 117, 117, 117, 117, 118, 118,
            118, 118, 118, 118, 119, 119, 119, 119, 120, 120, 120, 120, 120, 121, 121, 121, 121, 121, 121, 122, 122,
            122, 122, 123, 123, 123, 123, 123, 124, 124, 124};


    final int TRUE_NUM_BLOCKED_SQUARES = 291;

    Grid grid;
    double lat_top = 28.2440;
    double lat_bottom = 28.2172;
    double lng_left = 112.9220;
    double lng_right = 112.9540;
    double squareSize = 0.0002;
    int TRUE_NUM_SQUARES_X = 160;
    int TRUE_NUM_SQUARES_Y = 134;


    // test correct grid squares blocked

    @BeforeAll
    public void setUp() {

        grid = new Grid(squareSize, lat_top, lat_bottom, lng_left, lng_right);

        System.out.println("running before");

        //Get buildings(polygons) from geojson file
        ArrayList<Geometry> buildings = MapParser.getBuildings("buildings_China.geojson");
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

        Node topRightSquare = grid.getGrid()[TRUE_NUM_SQUARES_Y - 1][TRUE_NUM_SQUARES_X - 1];

        System.out.println("lng_right: " + lng_right + " lat_top: " + lat_top);
        System.out.println("grid_lng_right: " + topRightSquare.getBottomLeftCoord()[0] + squareSize + " grid_lat_top" +
                ":" + " " + topRightSquare.getBottomLeftCoord()[1] + squareSize);

        Node test00 = grid.coordinateToGridSquare(lng_left, lat_bottom);

        Node testNM = grid.coordinateToGridSquare(lng_right - 0.00001, lat_top - 0.00001);
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

        assertEquals(TRUE_NUM_BLOCKED_SQUARES, count);
    }


}