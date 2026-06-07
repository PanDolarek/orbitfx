package org.example.orbitfx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.geometry.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Planet {
    public String name;
    public double x,y;
    public double velX, velY;
    public double radius;
    public double mass;
    private final double resetMass;
    private final double resetRadius;
    public final Circle shape;
    private double startX;
    private double startY;
    private Polyline path = new Polyline();

    public Planet(String name, double startX, double startY, double radius, double mass, Color color) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.mass = mass;
        this.resetMass = mass;
        this.radius = radius;
        this.resetRadius = radius;
        this.velX = 0.0;
        this.velY = 0.0;
        this.shape = new Circle(x, y, radius, color);
        this.startX = startX;
        this.startY = startY;
        path.setStroke(color);
        path.setStrokeWidth(2);
    }

    public Polyline getPath() {
        return path;
    }

    public Circle getShape() {
        return shape;
    }

    public double getRadius(){
        return radius;
    }

    public double getMass(){
        return mass;
    }

    public void updatePosition(double mpp, double cx, double cy, double relX, double relY) {
        double relativeX = this.x - relX;
        double relativeY = this.y - relY;
        double screenX = cx + (relativeX / mpp);
        double screenY = cy + (relativeY / mpp);
        Circle c = this.getShape();
        c.setCenterX(screenX);
        c.setCenterY(screenY);
    }

    public void reset(double newX, double newY, double newVelX, double newVelY) {
        this.x = newX;
        this.y = newY;
        this.velX = newVelX;
        this.velY = newVelY;
        this.mass = resetMass;
        this.radius = resetRadius;
        this.shape.setCenterX(this.x);
        this.shape.setCenterY(this.y);
    }

    public void resetToStart() {
        this.x = this.startX;
        this.y = this.startY;
        this.velX = 0;
        this.velY = 0;
        this.mass = resetMass;
    }

    public int hashCode() { return Objects.hash(name, x, y, velX, velY, mass, shape);}

    private List<Point2D> pathHistory = new LinkedList<>();

    public void recordPosition() {
        pathHistory.add(new Point2D(x, y));

        if (pathHistory.size() > 400) {
            pathHistory.remove(0);
        }
    }

    public List<Point2D> getPathHistory() {
        return pathHistory;
    }

    private Planet parentPlanet = null;

    public void setParentPlanet(Planet parentPlanet){
        this.parentPlanet = parentPlanet;
    }

    public Planet getParentPlanet(){
        return this.parentPlanet;
    }

}

