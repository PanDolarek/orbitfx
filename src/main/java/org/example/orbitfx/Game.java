package org.example.orbitfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game extends Application {

    public List<Planet> allObjects = new ArrayList<>();
    public Planet focusedObject;
    public List<Planet> resetObjects = new ArrayList<>();

    public void clearTrails() {
        for (Planet obj : allObjects) {
            obj.getPath().getPoints().clear();
            obj.getPathHistory().clear();
        }
    }

    private final double G = 6.67430e-11;
    private double simSpeed = 86400.0;
    private String speedText = "1 Day/s";
    private boolean isPaused = false;
    private long lastTime = 0;
    private double timeBuffer = 0.0;
    private double trailAccumulator = 0.0;

    public void start(Stage stage) {
        stage.setTitle("OrbitFX");
        Pane simulationArea = new Pane();
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1280, 720);

        Planet sun = new Planet("Sun", 0.0, 0.0, 696340000.0, 1.989e30, Color.YELLOW);
        Planet mercury = new Planet("Mercury", 1.000e10, 4.490e10, 2439700.0, 3.285e23, Color.BROWN);
        Planet venus = new Planet("Venus", -7.125e10, 8.045e10, 6051800.0, 4.867e24, Color.ORANGE);
        Planet earth = new Planet("Earth", -3.293e10, 1.4336e11, 6371000.0, 5.972e24, Color.BLUE);
        Planet mars = new Planet("Mars", 1.888e11, -8.391e10, 3389500.0, 6.417e23, Color.RED);
        Planet jupiter = new Planet("Jupiter", 7.160e11, 1.885e11, 69911000.0, 1.898e27, Color.SADDLEBROWN);
        Planet saturn = new Planet("Saturn", -5.733e10, 1.3512e12, 58232000.0, 5.683e26, Color.YELLOW);
        Planet uranus = new Planet("Uranus", -2.706e12, 4.306e11, 25362000.0, 8.681e25, Color.CYAN);
        Planet neptune = new Planet("Neptune", 3.143e12, 3.140e12, 24622000.0, 1.024e26, Color.DARKBLUE);
        Planet ship = new Planet("Ship", -3.293e10, 1.4335e11, 1000000.0, 2600000, Color.VIOLET);

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
        resetObjects.addAll(allObjects);

        focusedObject = allObjects.getFirst();

        for (Planet obj : allObjects) {
            simulationArea.getChildren().add(obj.getPath());
            simulationArea.getChildren().add(obj.getShape());
            obj.getShape().setOnMouseClicked(event -> {
                focusedObject = obj;
            });
        }

        UIManager ui = new UIManager();

        root.setStyle("-fx-background-color: black;");
        root.getChildren().add(simulationArea);
        ui.attachTo(root);

        ui.realTimeBtn.setOnAction(event -> { simSpeed = 1.0; speedText = "1 s/s"; });
        ui.minBtn.setOnAction(event -> { simSpeed = 60.0; speedText = "1 Minute/s"; });
        ui.hourBtn.setOnAction(event -> { simSpeed = 3600.0; speedText = "1 Hour/s"; });
        ui.dayBtn.setOnAction(event -> { simSpeed = 86400.0; speedText = "1 Day/s"; });
        ui.weekBtn.setOnAction(event -> { simSpeed = 604800.0; speedText = "1 Week/s"; });
        ui.monthBtn.setOnAction(event -> { simSpeed = 2592000.0; speedText = "1 Month/s"; });
        ui.yearBtn.setOnAction(event -> { simSpeed = 31536000.0; speedText = "1 Year/s"; });
        //ui.tenYearsBtn.setOnAction(event -> simSpeed = 315360000.0);


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
                    ui.massField.setText(String.format(java.util.Locale.US, "%.3e", obj.mass).replaceFirst("e\\+0*", "e"));
                    ui.radiusField.setText(String.format(java.util.Locale.US, "%.3e", obj.radius).replaceFirst("e\\+0*", "e"));
                    double currentVel = Math.sqrt(obj.velX * obj.velX + obj.velY * obj.velY);
                    ui.velField.setText(String.format(java.util.Locale.US, "%.3e", currentVel).replaceFirst("e\\+0*", "e"));
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
        });

        AnimationTimer draw = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentWidth = simulationArea.getWidth() > 0 ? simulationArea.getWidth() : 1920.0;
                double currentHeight = simulationArea.getHeight() > 0 ? simulationArea.getHeight() : 1080.0;
                double centerX = currentWidth / 2.0;
                double centerY = currentHeight / 2.0;
                camera.updateScreenSize(currentWidth, currentHeight);
                Planet focus = camera.getFocusedObject();
                float vel = (float) Math.sqrt(focus.velX * focus.velX + focus.velY * focus.velY);

                ui.planetNameLabel.setText("Selected planet: " + focus.name);
                ui.infoLabel.setText(String.format("Current zoom out: %.2fx", camera.getMetersPerPixel()));
                ui.massLabel.setText(String.format("Mass: %6.3e [kg]", focus.mass));
                ui.radiusLabel.setText(String.format("Radius: %6.3e [m]", focus.radius));
                ui.velLabel.setText(String.format("Velocity: %6.3e [m/s]", vel));
                ui.timeLabel.setText("Current simulation speed: " + speedText);

                for (Planet obj : allObjects) {
                    obj.updatePosition(camera.getMetersPerPixel(), centerX, centerY, focus.x, focus.y);

                    double screenRadius = camera.getScreenRadius(obj.getRadius(), 4.0);
                    if (obj.getShape() != null) {
                        Circle circle = obj.getShape();
                        circle.setRadius(screenRadius);
                    }

                    Polyline trail = obj.getPath();

                    List<Double> flatPoints = new ArrayList<>(obj.getPathHistory().size() * 2);

                    for (Point2D point : obj.getPathHistory()) {
                        flatPoints.add(camera.getScreenX(point.getX()));
                        flatPoints.add(camera.getScreenY(point.getY()));
                    }
                    trail.getPoints().setAll(flatPoints);
                }
            }
        };

        ui.massField.setOnAction(e -> {
            try {
                Planet focused = camera.getFocusedObject();
                if (focused != null) {
                    focused.mass = Double.parseDouble(ui.massField.getText());
                }
            } catch (NumberFormatException ex) {
                System.err.println("Mass must be a number!");
            }
        });

        ui.radiusField.setOnAction(e -> {
            try {
                Planet focused = camera.getFocusedObject();
                if (focused != null) {
                    focused.radius = Double.parseDouble(ui.radiusField.getText());
                }
            } catch (NumberFormatException ex) {
                System.err.println("Radius must be a number!");
            }
        });

        ui.velField.setOnAction(e -> {
            try {
                double targetVelocity = Double.parseDouble(ui.velField.getText());
                Planet p = camera.getFocusedObject();

                if (p != null) {
                    double currentVelocity = Math.sqrt(p.velX * p.velX + p.velY * p.velY);

                    if (currentVelocity > 0) {
                        p.velX = p.velX * (targetVelocity / currentVelocity);
                        p.velY = p.velY * (targetVelocity / currentVelocity);
                    } else {
                        p.velX = targetVelocity;
                        p.velY = 0;
                    }
                }
            } catch (NumberFormatException ex) {
                System.err.println("Velocity must be a number!");
            }
        });

        AnimationTimer game = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double step;
                if(simSpeed == 31536000.0) {
                    step = 50.0;
                }
                else if (simSpeed > 86400.0) {
                    step = 10.0;
                } else {
                    step = 2.0;
                }

                if (isPaused) {
                    lastTime = now;
                    return;
                }
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double elapsedRealSeconds = (now - lastTime) / 1000000000.0;
                lastTime = now;
                if (elapsedRealSeconds > 0.1) {
                    elapsedRealSeconds = 0.1;
                }
                double simSecondsToProcess = elapsedRealSeconds * simSpeed;
                timeBuffer += simSecondsToProcess;

                while (timeBuffer >= step) {

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
                                a.velX += accA * (dx / dist) * step;
                                a.velY += accA * (dy / dist) * step;
                                b.velX -= accB * (dx / dist) * step;
                                b.velY -= accB * (dy / dist) * step;
                            } else {
                                double impactVelX = b.velX - a.velX;
                                double impactVelY = b.velY - a.velY;
                                double impactSpeed = Math.sqrt(impactVelX * impactVelX + impactVelY * impactVelY);

                                double totalMass = a.getMass() + b.getMass();
                                double newVelX = (a.getMass() * a.velX + b.getMass() * b.velX) / totalMass;
                                double newVelY = (a.getMass() * a.velY + b.getMass() * b.velY) / totalMass;
                                a.velX = newVelX;
                                a.velY = newVelY;
                                b.velX = newVelX;
                                b.velY = newVelY;

                                if (impactSpeed > 20 && a.radius >= b.radius) {
                                    a.mass += b.mass;
                                    a.radius += b.radius;
                                    b.getPath().getPoints().clear();
                                    allObjects.remove(b);
                                    simulationArea.getChildren().removeAll(b.getShape(), b.getPath());
                                }
                            }
                        }
                    }

                    for (Planet p : allObjects) {
                        p.x += p.velX * step;
                        p.y += p.velY * step;
                    }

                    timeBuffer -= step;
                    trailAccumulator += step;
                    double dynamicTrailStep = Math.max(step, Math.min(simSpeed / 60.0, 86400.0));

                    if (trailAccumulator >= dynamicTrailStep) {
                        for (Planet p : allObjects) {
                            p.recordPosition();
                        }
                        trailAccumulator = 0.0;
                    }
                }
            }
        };

        ui.pauseBtn.setOnAction(e -> {
            isPaused = !isPaused;
            ui.massField.setDisable(!isPaused);
            ui.radiusField.setDisable(!isPaused);
            ui.velField.setDisable(!isPaused);
            ui.pauseLabel.setVisible(isPaused);
        });

        ui.resetBtn.setOnAction(event -> {
            sun.reset(0.0, 0.0, 0.0, 0.0);
            mercury.reset(1.000e10, 4.490e10, -57567, 12815);
            venus.reset(-7.125e10, 8.045e10, -26399, -23380);
            earth.reset(-3.293e10, 1.4336e11, -29520, -6781);
            mars.reset(1.888e11, -8.391e10, 10761, 24215);
            jupiter.reset(7.160e11, 1.885e11, -3491, 13260);
            saturn.reset(-5.733e10, 1.3512e12, -10173, -431);
            uranus.reset(-2.706e12, 4.306e11, -1117, -7027);
            neptune.reset(3.143e12, 3.140e12, -3884, 3887);
            ship.reset(-3.293224e10, 1.433697e11, -36162, -8307);

            lastTime = 0;
            timeBuffer = 0.0;
            isPaused = true;

            ui.massField.setDisable(false);
            ui.radiusField.setDisable(false);
            ui.velField.setDisable(false);
            ui.pauseLabel.setVisible(true);

            allObjects.clear();
            allObjects.addAll(resetObjects);

            for (Planet p : allObjects) {
                if (!simulationArea.getChildren().contains(p.getShape())) {
                    simulationArea.getChildren().addAll(p.getShape(), p.getPath());
                }
                clearTrails();
            }

            Planet focus = camera.getFocusedObject();
            ui.massField.setText(String.format(java.util.Locale.US, "%.3e", focus.mass).replaceFirst("e\\+0*", "e"));
            ui.radiusField.setText(String.format(java.util.Locale.US, "%.3e", focus.radius).replaceFirst("e\\+0*", "e"));

            game.start();
        });

        draw.start();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}