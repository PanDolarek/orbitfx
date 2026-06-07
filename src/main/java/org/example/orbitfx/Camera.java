package org.example.orbitfx;

public class Camera {
    private double metersPerPixel;
    private double centerX;
    private double centerY;
    private Planet focusedObject;

    public Camera(double initialMetersPerPixel, double centerX, double centerY) {
        this.metersPerPixel = initialMetersPerPixel;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void zoomIn() {
        metersPerPixel *= 0.9;
    }

    public void zoomOut() {
        metersPerPixel *= 1.1;
    }

    public void setFocusedObject(Planet planet) {
        this.focusedObject = planet;
    }

    public Planet getFocusedObject() {
        return focusedObject;
    }

    public double getScreenX(double worldX) {
        if (focusedObject == null) return centerX;
        return centerX + (worldX - focusedObject.x) / metersPerPixel;
    }

    public double getScreenY(double worldY) {
        if (focusedObject == null) return centerY;
        return centerY + (worldY - focusedObject.y) / metersPerPixel;
    }

    public double getScreenRadius(double realRadius, double minPixels) {
        return Math.max(realRadius / metersPerPixel, minPixels);
    }

    public double getMetersPerPixel() {
        return metersPerPixel;
    }

    public void updateScreenSize(double newWidth, double newHeight) {
        this.centerX = newWidth / 2.0;
        this.centerY = newHeight / 2.0;
    }
}