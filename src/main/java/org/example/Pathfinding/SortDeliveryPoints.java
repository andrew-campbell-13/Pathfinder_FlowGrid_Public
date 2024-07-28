package org.example.Pathfinding;

import java.util.ArrayList;
import java.util.Arrays;

public class SortDeliveryPoints {

    //runtime- O(n^2)(2^n)
    static double[][] distances;
    static int numDps;
    static double[][] memoization;
    static int[][] parent;

    public static ArrayList runSorter(double[][] adjacencyMatrix) {

        numDps = adjacencyMatrix.length-1;
        distances = adjacencyMatrix;
        memoization = new double[numDps][1 << numDps];
        parent = new int[numDps][1 << numDps];

        solveTSP();
        ArrayList<Integer> optimalTour = new ArrayList<>();

        // ensure route starts and ends at the starting DP
        optimalTour.add(0);
        optimalTour.addAll(reconstructOptimalTour());
        optimalTour.add(0);

        return optimalTour;
    }

    public static void solveTSP() {
        // Initialize memoization table with -1
        for (int i = 0; i < numDps; i++) {
            Arrays.fill(memoization[i], -1);
        }

        // Start from the first DP (0) and visit all DPs
        tsp(0, 1);
    }

    public static double tsp(int currentDP, int visitedMask) {
        // If all DPs have been visited, return the distance to starting DP
        if (visitedMask == (1 << numDps) - 1) {
            return distances[currentDP][0];
        }

        // If the result is already memoized, return it
        if (memoization[currentDP][visitedMask] != -1) {
            return memoization[currentDP][visitedMask];
        }

        double minDistance = Double.MAX_VALUE;
        int bestNextDP = -1;

        // Consider visiting unvisited DPs
        for (int nextDP = 0; nextDP < numDps; nextDP++) {
            if ((visitedMask & (1 << nextDP)) == 0) {
                double newDistance = distances[currentDP][nextDP] + tsp(nextDP, visitedMask | (1 << nextDP));
                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    bestNextDP = nextDP;
                }
            }
        }

        // Memoize the result and update parent
        memoization[currentDP][visitedMask] = minDistance;
        parent[currentDP][visitedMask] = bestNextDP;
        return minDistance;
    }

    public static ArrayList<Integer> reconstructOptimalTour() {
        ArrayList<Integer> tour = new ArrayList<>();
        int currentDP = 0;
        int visitedMask = 1; // Start from the first DP

        while (visitedMask != (1 << numDps) - 1) {
            int nextDP = parent[currentDP][visitedMask];
            tour.add(nextDP);
            visitedMask |= (1 << nextDP);
            currentDP = nextDP;
        }

        return tour;
    }


}
