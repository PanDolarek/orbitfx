module org.example.orbitfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.orbitfx to javafx.fxml;
    exports org.example.orbitfx;
}