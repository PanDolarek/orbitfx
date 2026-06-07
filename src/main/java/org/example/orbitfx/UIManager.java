package org.example.orbitfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UIManager {

    public Button resetBtn = new Button("Start/Reset");
    public Button pauseBtn = new Button("Pause/Resume");
    public Button realTimeBtn = new Button("1 s/s");
    public Button minBtn = new Button("1 Minute/s");
    public Button hourBtn = new Button("1 Hour/s");
    public Button dayBtn = new Button("1 Day/s");
    public Button weekBtn = new Button("1 Week/s");
    public Button monthBtn = new Button("1 Month/s");
    public Button yearBtn = new Button("1 Year/s");
    //public Button tenYearsBtn = new Button("10 Years/s");
    public Label infoLabel = new Label();
    public Label velLabel = new Label();
    public Label massLabel = new Label();
    public Label radiusLabel = new Label();
    public Label timeLabel = new Label();
    public Label pauseLabel = new Label("PAUSED");
    public Label planetNameLabel = new Label("Selected planet: ");
    public TextField massField = new TextField("Click on any object");
    public TextField radiusField = new TextField("Click on any object");
    public TextField velField = new TextField("Click on any object");
    private HBox uiBar;
    private VBox infoBar;

    public UIManager() {
        setupUI();
    }

    private void setupUI() {
        uiBar = new HBox(15, resetBtn, pauseBtn, infoLabel, realTimeBtn, minBtn, hourBtn, dayBtn, weekBtn, monthBtn, yearBtn, timeLabel);
        infoBar = new VBox(20, planetNameLabel, velLabel, velField, massLabel, massField, radiusLabel, radiusField);
        infoLabel.setTextFill(Color.WHITE);
        planetNameLabel.setTextFill(Color.WHITE);
        velLabel.setTextFill(Color.WHITE);
        massLabel.setTextFill(Color.WHITE);
        radiusLabel.setTextFill(Color.WHITE);
        timeLabel.setTextFill(Color.LIGHTGREEN);
        pauseLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: red;");
        pauseLabel.setVisible(false);
        uiBar.setSpacing(20);
        uiBar.setAlignment(Pos.CENTER);
        uiBar.setMaxHeight(HBox.USE_PREF_SIZE);
        uiBar.setStyle("-fx-padding: 10px;");
        uiBar.setPickOnBounds(false);
        infoBar.setSpacing(20);
        infoBar.setAlignment(Pos.TOP_RIGHT);
        infoBar.setMaxWidth(300);
        infoBar.setMaxHeight(VBox.USE_PREF_SIZE);
        infoBar.setStyle("-fx-padding: 15px;");
        infoBar.setPickOnBounds(false);
        planetNameLabel.setMaxWidth(Double.MAX_VALUE);
        velLabel.setMaxWidth(Double.MAX_VALUE);
        massLabel.setMaxWidth(Double.MAX_VALUE);
        radiusLabel.setMaxWidth(Double.MAX_VALUE);
        planetNameLabel.setAlignment(Pos.CENTER);
        velLabel.setAlignment(Pos.CENTER);
        massLabel.setAlignment(Pos.CENTER);
        radiusLabel.setAlignment(Pos.CENTER);
    }

    public void attachTo(StackPane root) {
        StackPane.setAlignment(uiBar, Pos.TOP_CENTER);
        StackPane.setAlignment(infoBar, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBar, new Insets(60, 20, 0, 0));
        StackPane.setAlignment(pauseLabel, Pos.TOP_CENTER);
        StackPane.setMargin(pauseLabel, new Insets(70, 0, 0, 0));
        root.getChildren().addAll(uiBar, infoBar, pauseLabel);
    }
}