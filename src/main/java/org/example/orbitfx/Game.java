package org.example.orbitfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    public List<Planet> planets = new ArrayList<>();
    public List<Planet> allObjects = new ArrayList<>();

    private final double G = 6.67430e-11;
    private double dt = 3600;
    private double simSpeed = 1.0;
    private double centerX = 960.0;
    private double centerY = 540.0;
    private double metersPerPixel = 500000000.0 / 540.0;

    Button button = new Button();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Orbit");
        Pane simulationArea = new Pane();
        BorderPane root = new BorderPane();

        Planet ship = new Planet("Ship", 384400000.0, 0, 4, 2600000, Color.GREEN);
        //Planet ship = new Planet("Ship", 42000000.0, 0, 4, 2137, Color.GREEN);
        planets.add(new Planet("Earth", centerX, centerY, 12, 5.972e24, Color.BLUE));
        Planet moon = new Planet("Moon", 384400000.0, 0, 8, 7.347e22, Color.GRAY);

        //laptop Scene scene = new Scene(root, 1450, 750);
        Scene scene = new Scene(root, 1920, 1080);



        simulationArea.getChildren().addAll(ship.getShape());
        simulationArea.getChildren().addAll(moon.getShape());
        for(Planet p : planets){
            simulationArea.getChildren().add(p.getShape());
        }

        allObjects.clear();
        allObjects.addAll(planets);
        allObjects.add(ship);
        allObjects.add(moon);

        TextField velo = new TextField();
        Label infoLabel = new Label("Initial velocity = 0.0");

        Slider speedSlider = new Slider(0.1, 2.0, 1.0);
        Label speedLabel = new Label("Sim Speed: 1.00x");
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            simSpeed = newVal.doubleValue();
            speedLabel.setText(String.format("Sim Speed: %.2fx", simSpeed));
        });

        HBox uiBar = new HBox(10, velo, infoLabel, speedSlider, speedLabel);
        uiBar.setAlignment(Pos.CENTER);

        root.setTop(uiBar);
        root.setStyle("-fx-background: black");
        root.setCenter(simulationArea);

        AnimationTimer draw = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Planet earth = planets.get(0);
                double currentDt = dt * simSpeed;
                for (Planet obj : allObjects) {
                    obj.updatePosition(currentDt, metersPerPixel, centerX, centerY, ship.x, ship.y);
                }
            }
        };

        AnimationTimer calculate = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentDt = dt * simSpeed;
                for (Planet a : allObjects) {
                        for (Planet b : allObjects) {
                                if (a == b) continue;

                                double dx = b.x - a.x;
                                double dy = b.y - a.y;
                                double distSq = (dx * dx) + (dy * dy);
                                double dist = Math.sqrt(distSq);
                                double radB = b.getRadius() * metersPerPixel;
                                double radA = a.getRadius() * metersPerPixel;

                                if (dist > radB) {
                                    double gForce = (G * b.mass * a.mass) / distSq;
                                    double accA = gForce / a.mass;
                                    double accB = gForce / b.mass;

                                    a.velX += accA * (dx / dist) * currentDt;
                                    a.velY += accA * (dy / dist) * currentDt;

                                    b.velX -= accB * (dx / dist) * simSpeed;
                                    b.velY -= accB * (dy / dist) * simSpeed;
                                } else if (dist <= radB) {
                                    double newVelX = (a.getMass() * a.velX + b.getMass() * b.velX) / (a.getMass() + b.getMass());

                                    double newVelY = (a.getMass() * a.velY + b.getMass() * b.velY) / (a.getMass() + b.getMass());

                                    a.velX = newVelX;
                                    a.velY = newVelY;
                                    b.velX = newVelX;
                                    b.velY = newVelY;

                                }
                            }
                        }
                    }
        };

        velo.setOnAction(event -> {
            try {
                double newVelY = Double.parseDouble(velo.getText());

                ship.reset(404400000.0, 0, 0, newVelY);

                moon.reset(384400000.0, 0, 0, 1022.0);

                for(Planet p : planets){
                    p.resetToStart();
                }

                infoLabel.setText("Initial velocity = " + newVelY + " m/s");
                calculate.start();
                draw.start();
            } catch (NumberFormatException e) {
                infoLabel.setText("Error! Must be a number");
            }
            velo.clear();
        });

        stage.setScene(scene);
        stage.show();
    }
}