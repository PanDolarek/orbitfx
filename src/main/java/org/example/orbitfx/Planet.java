package org.example.orbitfx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class Planet {
    public String name;
    public double x,y;
    public double velX, velY;
    public double mass;
    public final Circle shape;

    public Planet(String name, double startX, double startY, double radius, double mass, Color color) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.mass = mass;
        this.velX = 0.0;
        this.velY = 0.0;
        this.shape = new Circle(x, y, radius, color);
    }

    public Circle getShape() {
        return shape;
    }

    public void updatePosition(double speedMultiplier) {
        this.x += this.velX * speedMultiplier;
        this.y += this.velY * speedMultiplier;

        this.shape.setCenterX(this.x);
        this.shape.setCenterY(this.y);
    }

    public void reset(double newX, double newY, double newVelX, double newVelY) {
        this.x = newX;
        this.y = newY;
        this.velX = newVelX;
        this.velY = newVelY;
        this.shape.setCenterX(this.x);
        this.shape.setCenterY(this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Planet planet)) return false;
        return Double.compare(x, planet.x) == 0 && Double.compare(y, planet.y) == 0 && Double.compare(velX, planet.velX) == 0 && Double.compare(velY, planet.velY) == 0 && Double.compare(mass, planet.mass) == 0 && Objects.equals(name, planet.name) && Objects.equals(shape, planet.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, x, y, velX, velY, mass, shape);
    }
}

