package org.example.javafxhibernate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.DAO.SocioDAO;
import org.example.Entities.Socio;

import java.util.List;
import java.util.Optional;

public class SocioController {
    @FXML
    private TableView<Socio> socioTable;

    @FXML
    private TableColumn<Socio, Integer> colId;

    @FXML
    private TableColumn<Socio, String> colNombre;

    @FXML
    private TableColumn<Socio, String> colDireccion;

    @FXML
    private TableColumn<Socio, String> colTelefono;

    @FXML
    private TextField tfNombre;

    @FXML
    private TextField tfDireccion;

    @FXML
    private TextField tfTelefono;

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

    private SocioDAO socioDAO;
    private ObservableList<Socio> sociosObservable;

    @FXML
    public void initialize() {
        socioDAO = new SocioDAO();
        setupTableColumns();
        loadAllAutores();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Agregar listener para cargar datos cuando se selecciona un socio
        socioTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void loadAllAutores() {
        List<Socio> socios = socioDAO.findAll();
        sociosObservable = FXCollections.observableArrayList(socios);
        socioTable.setItems(sociosObservable);
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
        String direccion = tfDireccion.getText().trim();
        String telefono = tfTelefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            Socio nuevoSocio = new Socio(nombre, direccion, telefono);
            socioDAO.create(nuevoSocio);
            mostrarAlerta("Éxito", "Socio agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllAutores();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el socio: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarAutor() {
        Socio socioSeleccionado = socioTable.getSelectionModel().getSelectedItem();

        if (socioSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un socio de la tabla", Alert.AlertType.ERROR);
            return;
        }

        String nombre = tfNombre.getText().trim();
        String direccion = tfDireccion.getText().trim();
        String telefono = tfTelefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            socioSeleccionado.setNombre(nombre);
            socioSeleccionado.setDireccion(direccion);
            socioSeleccionado.setTelefono(telefono);
            socioDAO.update(socioSeleccionado);
            mostrarAlerta("Éxito", "Socio modificado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllAutores();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo modificar el socio: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarAutor() {
        Socio autorSeleccionado = socioTable.getSelectionModel().getSelectedItem();

        if (autorSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un socio de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este socio?");
        confirmacion.setContentText("Se eliminará: " + autorSeleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                socioDAO.deleteById(autorSeleccionado.getId());
                mostrarAlerta("Éxito", "Socio eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                loadAllAutores();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el socio: " + e.getMessage(), Alert.AlertType.ERROR);
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
            List<Socio> socios = socioDAO.findByNombre(nombreBusqueda);
            if (socios.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron socios con ese nombre", Alert.AlertType.INFORMATION);
            } else {
                sociosObservable = FXCollections.observableArrayList(socios);
                socioTable.setItems(sociosObservable);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario() {
        tfNombre.clear();
        tfDireccion.clear();
        tfTelefono.clear();
        tfBuscar.clear();
        socioTable.getSelectionModel().clearSelection();
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
        Socio socioSeleccionado = socioTable.getSelectionModel().getSelectedItem();
        if (socioSeleccionado != null) {
            tfNombre.setText(socioSeleccionado.getNombre());
            tfDireccion.setText(socioSeleccionado.getDireccion());
            tfTelefono.setText(socioSeleccionado.getTelefono());
        }
    }
}
