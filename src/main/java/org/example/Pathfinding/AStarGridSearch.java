package org.example.Pathfinding;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.example.MapObjects.Node;

public class AStarGridSearch {

    public static List<Node> findPath(Node[][] grid, Node startNode, Node targetNode) {
        List<Node> openSet = new ArrayList<>();
        List<Node> closedSet = new ArrayList<>();

        startNode.setGScore(0);
        startNode.setHScore(calculateHScore(startNode, targetNode));
        startNode.setFScore(startNode.calculateFScore());
        openSet.add(startNode);

        while (!openSet.isEmpty()) {

            Node current = getLowestFScoreNode(openSet);
            if (current == targetNode) {
                //create node path from startNode to targetNode
                List<Node> nodePath = new ArrayList<Node>(reconstructPath(targetNode));

                //reset nodes so they can be used for search between next two nodes
                resetNodes(openSet);
                resetNodes(closedSet);

                return nodePath;
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Node neighbor : getNeighbors(grid, current)) {
                if ((neighbor.isBlocked() &&  !neighbor.isTarget())|| closedSet.contains(neighbor)) {
                    continue;
                }

                neighbor.setIsExplored();



                double tentativeGScore = current.getGScore() + calculateGScore(current, neighbor);
                if (!openSet.contains(neighbor) || tentativeGScore < neighbor.getGScore()) {
                    neighbor.setParent(current);
                    neighbor.setGScore(tentativeGScore);
                    neighbor.setHScore(calculateHScore(neighbor, targetNode));
                    neighbor.setFScore(neighbor.calculateFScore());

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        resetNodes(openSet);
        resetNodes(closedSet);

        return null; // No path found
    }

    private static double calculateHScore(Node node, Node targetNode) {
        // Manhattan distance heuristic
        return Math.abs(node.getX() - targetNode.getX()) + Math.abs(node.getY() - targetNode.getY());
    }

    private static double calculateGScore(Node start, Node neighbor) {
        // Cost of moving from 'start' to 'neighbor'
        return 1.01;
    }

    private static Node getLowestFScoreNode(List<Node> nodes) {
        return Collections.min(nodes, (node1, node2) -> Double.compare(node1.getFScore(), node2.getFScore()));
    }

    private static List<Node> getNeighbors(Node[][] grid, Node node) {
        // Implement how to get neighbors of a given node
        // This will depend on your specific grid topology (4-way or 8-way movement)
        // Return a list of walkable neighbors.

        List<Node> neighbours = new ArrayList<>();

        // get position of current node in grid
        int x = node.getX();
        int y = node.getY();

        //iterate through possible neighbours, add neighbour node if in grid parameters
        for (int i = y-1; i < y+2; i++) {
            for(int j= x-1; j<x+2; j++){
                // check neighbour cell exists in grid
                if(isValidCell(grid, j, i)){
                    neighbours.add(grid[i][j]);
                }
            }
        }

        return neighbours;
    }

    public static boolean isValidCell(Node[][] grid, int x, int y) {
        return x>=0 && y>=0 && x< grid[0].length && y<grid.length;
    }

    private static List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node.setPathNode(true);
            node = node.getParent();
        }
        Collections.reverse(path);

        return path;

    }

    private static void resetNodes(List<Node> nodes){
        for(Node node: nodes){
            node.resetNode();
        }
    }

}
