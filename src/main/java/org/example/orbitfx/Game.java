package org.example.orbitfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    public List<Planet> planets = new ArrayList<>();
    public List<Planet> allObjects = new ArrayList<>();
    public Planet focusedObject;

    public void clearTrails() {
        for (Planet obj : allObjects) {
            obj.getPath().getPoints().clear();
        }
    }

    private final double G = 6.67430e-11;
    private final double dt = 3600;
    private double simSpeed = 1.0;

    Button startButton = new Button("start/reset");
    Button lowXButton = new Button("0.01x");
    Button oneXButton = new Button("1x");
    Button fiveXButton = new Button("5x");
    Button tenXButton = new Button("10x");
    Button twentyXButton = new Button("20x");
    Button fiftyXButton = new Button("50x");
    Button hundredXButton = new Button("100x");

    public void start(Stage stage) {
        stage.setTitle("Orbit");
        Pane simulationArea = new Pane();
        BorderPane root = new BorderPane();
        //Scene scene = new Scene(root, 1450, 750);
        Scene scene = new Scene(root, 1920, 1080);

        Planet sun = new Planet("Sun", 0.0, 0.0, 696340000.0, 1.989e30, Color.YELLOW);
        Planet mercury = new Planet("Mercury", 5.79e10, 0.0, 2439700.0, 3.285e23, Color.BROWN);
        Planet venus = new Planet("Venus", 1.082e11, 0.0, 6051800.0, 4.867e24, Color.ORANGE);
        Planet earth = new Planet("Earth", 1.496e11, 0.0, 6371000.0, 5.972e24, Color.BLUE);
        Planet mars = new Planet("Mars", 2.279e11, 0.0, 3389500.0, 6.417e23, Color.RED);
        Planet jupiter = new Planet("Jupiter", 7.786e11, 0.0, 69911000.0, 1.898e27, Color.SADDLEBROWN);
        //Planet jupiter = new Planet("Jupiter", 7.786e11, 0.0, 69911000.0, 1.989e30, Color.SADDLEBROWN);
        Planet saturn = new Planet("Saturn", 1.433e12, 0.0, 58232000.0, 5.683e26, Color.YELLOW);
        Planet uranus = new Planet("Uranus", 2.872e12, 0.0, 25362000.0, 8.681e25, Color.CYAN);
        Planet neptune = new Planet("Neptune", 4.495e12, 0.0, 24622000.0, 1.024e26, Color.DARKBLUE);
        Planet ship = new Planet("Ship", 1.496e11, -10000000.0, 1000000.0, 2600000, Color.VIOLET);

        allObjects.clear();
        allObjects.add(sun);
        allObjects.add(mercury);
        allObjects.add(venus);
        allObjects.add(earth);
        allObjects.add(mars);
        allObjects.add(jupiter);
        allObjects.add(saturn);
        allObjects.add(uranus);
        allObjects.add(neptune);
        allObjects.add(ship);

        focusedObject = allObjects.getFirst();

        for(Planet obj : allObjects){
            simulationArea.getChildren().add(obj.getPath());
            simulationArea.getChildren().add(obj.getShape());
            obj.getShape().setOnMouseClicked(event -> {
                focusedObject = obj;
                clearTrails();
            });
        }

        Label infoLabel = new Label();

        lowXButton.setOnAction(ActionEvent->{
            simSpeed = 0.01;
        });

        oneXButton.setOnAction(ActionEvent->{
            simSpeed = 1.00;
        });

        fiveXButton.setOnAction(ActionEvent->{
            simSpeed = 5.00;
        });

        tenXButton.setOnAction(ActionEvent->{
            simSpeed = 10.00;
        });

        twentyXButton.setOnAction(ActionEvent->{
            simSpeed = 20.00;
        });

        fiftyXButton.setOnAction(ActionEvent->{
            simSpeed = 50.00;
        });

        hundredXButton.setOnAction(ActionEvent->{
            simSpeed = 100.00;
        });

        HBox uiBar = new HBox(20, startButton, infoLabel, lowXButton, oneXButton, fiveXButton, tenXButton, twentyXButton, fiftyXButton, hundredXButton);
        uiBar.setSpacing(20);
        uiBar.setAlignment(Pos.CENTER);
        root.setTop(uiBar);
        root.setStyle("-fx-background: black");
        root.setCenter(simulationArea);

        double initialScale = 4.5e12 / 900.0;
        Camera camera = new Camera(initialScale, 960.0, 540.0);
        camera.setFocusedObject(allObjects.getFirst());

        simulationArea.setOnMouseClicked(event -> {
            double clickX = event.getX();
            double clickY = event.getY();

            for (Planet obj : allObjects) {
                double screenX = camera.getScreenX(obj.x);
                double screenY = camera.getScreenY(obj.y);
                double dx = clickX - screenX;
                double dy = clickY - screenY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= 30.0) {
                    camera.setFocusedObject(obj);
                    clearTrails();
                    break;
                }
            }
        });

        simulationArea.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                camera.zoomIn();
            } else {
                camera.zoomOut();
            }
            clearTrails();
        });

        AnimationTimer draw = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Planet focus = camera.getFocusedObject();
                infoLabel.setText(String.format("Current zoom out: %.2fx", camera.getMetersPerPixel()));
                for (Planet obj : allObjects) {
                    obj.updatePosition(camera.getMetersPerPixel(), 960.0, 540.0, focus.x, focus.y);
                    obj.recordPosition();

                    double screenRadius = camera.getScreenRadius(obj.getRadius(), 4.0);
                    if (obj.getShape() != null) {
                        Circle circle = obj.getShape();
                        circle.setRadius(screenRadius);
                    }

                    Polyline trail = obj.getPath();
                    trail.getPoints().clear();

                    for (Point2D point : obj.getPathHistory()) {
                        double pScreenX = camera.getScreenX(point.getX());
                        double pScreenY = camera.getScreenY(point.getY());
                        trail.getPoints().addAll(pScreenX, pScreenY);
                    }
                }
            }
        };

        AnimationTimer game = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentDt = dt * simSpeed;
                int steps = 1000;
                double stepDt = currentDt / steps;

                for (int s = 0; s < steps; s++) {
                    for (int i = 0; i < allObjects.size(); i++) {
                        for (int j = i + 1; j < allObjects.size(); j++) {
                            Planet a = allObjects.get(i);
                            Planet b = allObjects.get(j);
                            double dx = b.x - a.x;
                            double dy = b.y - a.y;
                            double distSq = (dx * dx) + (dy * dy);
                            double dist = Math.sqrt(distSq);
                            double minCollisionDist = a.getRadius() + b.getRadius();

                            if (dist > minCollisionDist) {
                                double accA = (G * b.getMass()) / distSq;
                                double accB = (G * a.getMass()) / distSq;
                                a.velX += accA * (dx / dist) * stepDt;
                                a.velY += accA * (dy / dist) * stepDt;
                                b.velX -= accB * (dx / dist) * stepDt;
                                b.velY -= accB * (dy / dist) * stepDt;
                            } else {
                                double totalMass = a.getMass() + b.getMass();
                                double newVelX = (a.getMass() * a.velX + b.getMass() * b.velX) / totalMass;
                                double newVelY = (a.getMass() * a.velY + b.getMass() * b.velY) / totalMass;
                                a.velX = newVelX;
                                a.velY = newVelY;
                                b.velX = newVelX;
                                b.velY = newVelY;
                                if(Math.sqrt(a.velX * a.velX + a.velY * a.velY) > 20 && a.radius > b.radius){
                                    a.mass += b.mass;
                                    a.radius += b.radius;
                                    b.mass = 0;
                                    b.radius = 0;
                                    allObjects.remove(allObjects.get(j));
                                }
                            }
                        }
                    }

                    for (Planet p : allObjects) {
                        p.x += p.velX * stepDt;
                        p.y += p.velY * stepDt;
                    }
                }
            }
        };

        startButton.setOnAction(event -> {
                sun.reset(0.0, 0, 0, 0);
                mercury.reset(5.79e10, 0, 0, 47400);
                venus.reset(1.082e11, 0, 0, 35000);
                earth.reset(1.496e11, 0, 0, 30000);
                mars.reset(2.279e11, 0, 0, 24100);
                jupiter.reset(7.786e11, 0, 0, 13100);
                //jupiter.reset(7.786e11, 0, 0, 5400);
                saturn.reset(1.433e12, 0, 0, 9700);
                uranus.reset(2.872e12, 0, 0, 6800);
                neptune.reset(4.495e12, 0, 0, 5400);
                ship.reset(1.496e11, -10000000.0, 6313, 30000);

                for(Planet p : planets){
                    p.resetToStart();
                }
                game.start();
                clearTrails();
        });

        draw.start();
        stage.setScene(scene);
        stage.show();
    }
}