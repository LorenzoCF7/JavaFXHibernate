package org.example.javafxhibernate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.DAO.AutorDAO;
import org.example.Entities.Autor;

import java.util.List;
import java.util.Optional;

public class AutorController {
    @FXML
    private TableView<Autor> autorTable;
    
    @FXML
    private TableColumn<Autor, Integer> colId;
    
    @FXML
    private TableColumn<Autor, String> colNombre;
    
    @FXML
    private TableColumn<Autor, String> colNacionalidad;
    
    @FXML
    private TextField tfNombre;
    
    @FXML
    private TextField tfNacionalidad;
    
    @FXML
    private TextField tfBuscar;
    
    @FXML
    private Button btnAgregar;
    
    @FXML
    private Button btnModificar;
    
    @FXML
    private Button btnEliminar;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpiar;
    
    @FXML
    private Button btnListarTodos;
    
    private AutorDAO autorDAO;
    private ObservableList<Autor> autoresObservable;

    @FXML
    public void initialize() {
        autorDAO = new AutorDAO();
        setupTableColumns();
        loadAllAutores();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));
        
        // Agregar listener para cargar datos cuando se selecciona un autor
        autorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void loadAllAutores() {
        List<Autor> autores = autorDAO.findAll();
        autoresObservable = FXCollections.observableArrayList(autores);
        autorTable.setItems(autoresObservable);
    }

    private void setupButtonHandlers() {
        btnAgregar.setOnAction(e -> agregarAutor());
        btnModificar.setOnAction(e -> modificarAutor());
        btnEliminar.setOnAction(e -> eliminarAutor());
        btnBuscar.setOnAction(e -> buscarAutor());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnListarTodos.setOnAction(e -> loadAllAutores());
    }

    @FXML
    private void agregarAutor() {
        String nombre = tfNombre.getText().trim();
        String nacionalidad = tfNacionalidad.getText().trim();

        if (nombre.isEmpty() || nacionalidad.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            Autor nuevoAutor = new Autor(nombre, nacionalidad);
            autorDAO.create(nuevoAutor);
            mostrarAlerta("Éxito", "Autor agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllAutores();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el autor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarAutor() {
        Autor autorSeleccionado = autorTable.getSelectionModel().getSelectedItem();

        if (autorSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un autor de la tabla", Alert.AlertType.ERROR);
            return;
        }

        String nombre = tfNombre.getText().trim();
        String nacionalidad = tfNacionalidad.getText().trim();

        if (nombre.isEmpty() || nacionalidad.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            autorSeleccionado.setNombre(nombre);
            autorSeleccionado.setNacionalidad(nacionalidad);
            autorDAO.update(autorSeleccionado);
            mostrarAlerta("Éxito", "Autor modificado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllAutores();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo modificar el autor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarAutor() {
        Autor autorSeleccionado = autorTable.getSelectionModel().getSelectedItem();

        if (autorSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un autor de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este autor?");
        confirmacion.setContentText("Se eliminará: " + autorSeleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                autorDAO.deleteById(autorSeleccionado.getId());
                mostrarAlerta("Éxito", "Autor eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                loadAllAutores();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el autor: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void buscarAutor() {
        String nombreBusqueda = tfBuscar.getText().trim();

        if (nombreBusqueda.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa un nombre para buscar", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<Autor> autores = autorDAO.findByNombre(nombreBusqueda);
            if (autores.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron autores con ese nombre", Alert.AlertType.INFORMATION);
            } else {
                autoresObservable = FXCollections.observableArrayList(autores);
                autorTable.setItems(autoresObservable);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario() {
        tfNombre.clear();
        tfNacionalidad.clear();
        tfBuscar.clear();
        autorTable.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cargarDatosSeleccionados() {
        Autor autorSeleccionado = autorTable.getSelectionModel().getSelectedItem();
        if (autorSeleccionado != null) {
            tfNombre.setText(autorSeleccionado.getNombre());
            tfNacionalidad.setText(autorSeleccionado.getNacionalidad());
        }
    }
}
