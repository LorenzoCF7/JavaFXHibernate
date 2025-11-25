package org.example.javafxhibernate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private Button btnAutores;
    
    @FXML
    private Button btnLibros;
    
    @FXML
    private Button btnPrestamos;
    
    @FXML
    private Button btnSocios;

    @FXML
    public void initialize() {
        btnAutores.setOnAction(e -> loadView("autor-view.fxml"));
        btnLibros.setOnAction(e -> loadView("libro-view.fxml"));
        btnPrestamos.setOnAction(e -> loadView("prestamo-view.fxml"));
        btnSocios.setOnAction(e -> loadView("socio-view.fxml"));
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
