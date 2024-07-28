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
class GridTestFrance {

    final int[] TRUE_BLOCKED_XS = {51, 52, 53, 54, 55, 50, 51, 54, 55, 49, 50, 53, 54, 48, 49, 50, 51, 52, 53, 51, 52
            , 67, 68, 69, 70, 64, 65, 66, 67, 70, 64, 70, 25, 26, 27, 28, 29, 30, 31, 64, 65, 66, 67, 68, 69, 70, 25,
            31, 64, 65, 25, 26, 27, 28, 31, 28, 29, 30, 31};

    final int[] TRUE_BLOCKED_YS = {3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 22, 22, 22, 22, 23,
            23, 23, 23, 23, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 27, 27,
            27, 27, 27, 28, 28, 28, 28,};

    final int TRUE_NUM_BLOCKED_SQUARES =  59;

    Grid grid;
    double lat_top = 44.8364;
    double lat_bottom = 44.8276;
    double lng_left = -0.5864;
    double lng_right = -0.5696;
    double squareSize = 0.0002;
    int TRUE_NUM_SQUARES_X = 84;
    int TRUE_NUM_SQUARES_Y = 44;


    // test correct grid squares blocked

    @BeforeAll
    public void setUp() {

        grid = new Grid(squareSize, lat_top, lat_bottom, lng_left, lng_right);

        System.out.println("running before");

        //Get buildings(polygons) from geojson file
        ArrayList<Geometry> buildings = MapParser.getBuildings("buildings_France.geojson");
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

        Node topRightSquare = grid.getGrid()[TRUE_NUM_SQUARES_Y-1][TRUE_NUM_SQUARES_X-1];

        System.out.println("lng_right: " + lng_right + " lat_top: " + lat_top);
        System.out.println("grid_lng_right: " + topRightSquare.getBottomLeftCoord()[0]+squareSize + " grid_lat_top: " + topRightSquare.getBottomLeftCoord()[1]+squareSize );

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

        assertEquals(TRUE_NUM_BLOCKED_SQUARES, count);
    }


}