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

    Planet ship = new Planet("Ship",350, 200, 10, 1.0, Color.GREEN);

    private final double G = 2.0;
    private double simSpeed = 0.1;
    Button button = new Button();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Orbit");
        Pane simulationArea = new Pane();
        BorderPane root = new BorderPane();

        planets.add(new Planet("Earth", 250, 200, 50, 1000.0, Color.BLUE));
        planets.add(new Planet("Mars", 500, 500, 50, 1000.0, Color.RED));
        planets.add(new Planet("Venus", 750, 750, 50, 3000.0, Color.YELLOW));

        Scene scene = new Scene(root, 1000, 1000);



        simulationArea.getChildren().addAll(ship.getShape());
        for(Planet p : planets){
            simulationArea.getChildren().add(p.getShape());
        }

        simulationArea.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    System.out.println("KLIK" + mouseEvent.getX() + " " + mouseEvent.getY());
                    Planet newPlanet = new Planet("Black but actually white hole?", mouseEvent.getX(), mouseEvent.getY(), 50, 5000.0, Color.WHITE);
                    planets.add(newPlanet);
                    allObjects.add(newPlanet);
                    simulationArea.getChildren().add(newPlanet.getShape());
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    Planet planetToRemove = null;
                    for (Planet p : planets) {
                        double dx = p.x - mouseEvent.getX();
                        double dy = p.y - mouseEvent.getY();
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance <= 50) {
                            planetToRemove = p;
                            break;
                        }
                    }
                    if (planetToRemove != null) {
                        planets.remove(planetToRemove);
                        allObjects.remove(planetToRemove);
                        simulationArea.getChildren().remove(planetToRemove.getShape());
                    }
                }
            }
        });
        allObjects.clear();
        allObjects.addAll(planets);
        allObjects.add(ship);

        TextField velo = new TextField();
        Label infoLabel = new Label("Initial velocity = 0.0");

        Slider speedSlider = new Slider(0.1, 2.0, 0.1);
        Label speedLabel = new Label("Sim Speed: 0.1x");
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            simSpeed = newVal.doubleValue();
            speedLabel.setText(String.format("Sim Speed: %.2fx", simSpeed));
        });

        HBox uiBar = new HBox(10, velo, infoLabel, speedSlider, speedLabel);
        uiBar.setAlignment(Pos.CENTER);

        root.setTop(uiBar);
        root.setStyle("-fx-background: black");
        root.setCenter(simulationArea);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Planet a : allObjects) {
                        for (Planet b : allObjects) {
                            if (a == b) continue;

                            double dx = b.x - a.x;
                            double dy = b.y - a.y;
                            double distSq = (dx*dx) + (dy*dy);
                            double dist = Math.sqrt(distSq);

                            if (dist > 50) {
                                double gForce = (G * b.mass) / distSq;
                                a.velX += gForce * (dx / dist) * simSpeed;
                                a.velY += gForce * (dy / dist) * simSpeed;
                            }else{
                                a.velY = 0;
                                a.velX = 0;
                            }
                        }
                }

                for (Planet obj : allObjects) {
                    obj.updatePosition(simSpeed);
                }
            }
        };

        velo.setOnAction(event -> {
            try {
                double newVelY = Double.parseDouble(velo.getText());
                ship.reset(350, 200, 0, newVelY);
                infoLabel.setText("Initial velocity = " + newVelY);
                timer.start();
            } catch (NumberFormatException e) {
                infoLabel.setText("Error! Must be an integer/double");
            }
            velo.clear();
        });


        stage.setScene(scene);
        stage.show();
    }
}