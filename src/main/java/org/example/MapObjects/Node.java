package org.example.MapObjects;

public class Node {

    private final int x;
    private final int y;
    private double[] bottomLeftCoord = new double[2];
    private boolean isTarget;
    private double gScore;
    private double hScore;
    private double fScore;

    private boolean isPathNode;

    private boolean isBlocked;

    private boolean isExplored;

    private Node parent;

    // a node is visually represented by a square in the grid
    public Node(double[] coordinate, int x, int y) {
        this.bottomLeftCoord = coordinate;
        this.x = x;
        this.y = y;
        this.isBlocked = false;
        this.isTarget = false;
        this.gScore = Double.POSITIVE_INFINITY;
        this.hScore = 0;
        this.fScore = Double.POSITIVE_INFINITY;
        this.parent = null;
        this.isExplored = false;

    }

    public double[] getBottomLeftCoord() {
        return bottomLeftCoord;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean val) {
        isBlocked = val;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getFScore() {
        return fScore;
    }

    public void setFScore(double val) {
        fScore = val;
    }

    public double getGScore() {
        return gScore;
    }

    public void setGScore(double val) {
        gScore = val;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node node) {
        parent = node;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public boolean isPathNode() {
        return isPathNode;
    }

    public void setPathNode(boolean val) {
        this.isPathNode = val;
    }

    public void setHScore(double val) {
        hScore = val;
    }

    public double calculateFScore() {
        return gScore + hScore;
    }

    public void setIsExplored() {
        this.isExplored = true;
    }

    public void resetNode() {
        this.isBlocked = false;
        this.isTarget = false;
        this.gScore = Double.POSITIVE_INFINITY;
        this.hScore = 0;
        this.fScore = Double.POSITIVE_INFINITY;
        this.parent = null;
    }
}
