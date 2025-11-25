package org.example.javafxhibernate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.DAO.SocioDAO;
import org.example.Entities.Libro;
import org.example.Entities.Prestamo;
import org.example.Entities.Socio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrestamoController {

    @FXML
    private TableView<Prestamo> prestamoTable;

    @FXML
    private TableColumn<Prestamo, Integer> colId;

    @FXML
    private TableColumn<Prestamo, String> colSocio;

    @FXML
    private TableColumn<Prestamo, String> colLibro;

    @FXML
    private TableColumn<Prestamo, LocalDate> colFechaPrestamo;

    @FXML
    private TableColumn<Prestamo, LocalDate> colFechaDevolucion;

    @FXML
    private ComboBox<Socio> cbSocio;

    @FXML
    private ComboBox<Libro> cbLibro;

    @FXML
    private DatePicker dpFechaPrestamo;

    @FXML
    private DatePicker dpFechaDevolucion;

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

    private PrestamoDAO prestamoDAO;
    private SocioDAO socioDAO;
    private LibroDAO libroDAO;
    private ObservableList<Prestamo> prestamoObservable;
    private ObservableList<Socio> sociosObservable;
    private ObservableList<Libro> librosObservable;

    @FXML
    public void initialize() {
        prestamoDAO = new PrestamoDAO();
        socioDAO = new SocioDAO();
        libroDAO = new LibroDAO();
        setupTableColumns();
        loadSociosComboBox();
        loadLibrosComboBox();
        loadAllPrestamos();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSocio.setCellValueFactory(cellData -> {
            Socio socio = cellData.getValue().getSocio();
            return new javafx.beans.property.SimpleStringProperty(socio != null ? socio.getNombre() : "");
        });
        colLibro.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue().getLibro();
            return new javafx.beans.property.SimpleStringProperty(libro != null ? libro.getTitulo() : "");
        });
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fecha_prestamo"));
        colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fecha_devolucion"));
        prestamoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void loadSociosComboBox() {
        List<Socio> socios = socioDAO.findAll();
        sociosObservable = FXCollections.observableArrayList(socios);
        cbSocio.setItems(sociosObservable);
        cbSocio.setCellFactory(param -> new ListCell<Socio>() {
            @Override
            protected void updateItem(Socio item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
        cbSocio.setButtonCell(new ListCell<Socio>() {
            @Override
            protected void updateItem(Socio item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
    }

    private void loadLibrosComboBox() {
        List<Libro> libros = libroDAO.findAll();
        librosObservable = FXCollections.observableArrayList(libros);
        cbLibro.setItems(librosObservable);
        cbLibro.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitulo());
            }
        });
        cbLibro.setButtonCell(new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitulo());
            }
        });
    }

    private void loadAllPrestamos() {
        List<Prestamo> prestamos = prestamoDAO.findAll();
        prestamoObservable = FXCollections.observableArrayList(prestamos);
        prestamoTable.setItems(prestamoObservable);
    }

    private void setupButtonHandlers() {
        btnAgregar.setOnAction(e -> agregarPrestamo());
        btnModificar.setOnAction(e -> modificarPrestamo());
        btnEliminar.setOnAction(e -> eliminarPrestamo());
        btnBuscar.setOnAction(e -> buscarPrestamo());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnListarTodos.setOnAction(e -> loadAllPrestamos());
    }

    @FXML
    private void agregarPrestamo() {
        Socio socio = cbSocio.getValue();
        Libro libro = cbLibro.getValue();
        LocalDate fechaPrest = dpFechaPrestamo.getValue();
        LocalDate fechaDev = dpFechaDevolucion.getValue();

        if (socio == null || libro == null || fechaPrest == null || fechaDev == null) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        if (fechaDev.isBefore(fechaPrest)) {
            mostrarAlerta("Error", "La fecha de devolución debe ser posterior a la de préstamo", Alert.AlertType.ERROR);
            return;
        }

        try {
            Prestamo nuevoPrestamo = new Prestamo(socio, libro, fechaPrest, fechaDev);
            prestamoDAO.create(nuevoPrestamo);
            mostrarAlerta("Éxito", "Préstamo agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllPrestamos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el préstamo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarPrestamo() {
        Prestamo prestamoSeleccionado = prestamoTable.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un préstamo de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Socio socio = cbSocio.getValue();
        Libro libro = cbLibro.getValue();
        LocalDate fechaPrest = dpFechaPrestamo.getValue();
        LocalDate fechaDev = dpFechaDevolucion.getValue();

        if (socio == null || libro == null || fechaPrest == null || fechaDev == null) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        if (fechaDev.isBefore(fechaPrest)) {
            mostrarAlerta("Error", "La fecha de devolución debe ser posterior a la de préstamo", Alert.AlertType.ERROR);
            return;
        }

        try {
            prestamoSeleccionado.setSocio(socio);
            prestamoSeleccionado.setLibro(libro);
            prestamoSeleccionado.setFecha_prestamo(fechaPrest);
            prestamoSeleccionado.setFecha_devolucion(fechaDev);
            prestamoDAO.update(prestamoSeleccionado);
            mostrarAlerta("Éxito", "Préstamo modificado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllPrestamos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo modificar el préstamo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarPrestamo() {
        Prestamo prestamoSeleccionado = prestamoTable.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un préstamo de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este préstamo?");
        confirmacion.setContentText("Se eliminará el préstamo ID: " + prestamoSeleccionado.getId());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                prestamoDAO.deleteById(prestamoSeleccionado.getId());
                mostrarAlerta("Éxito", "Préstamo eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                loadAllPrestamos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el préstamo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void buscarPrestamo() {
        String nombreSocio = tfBuscar.getText().trim();

        if (nombreSocio.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa el nombre del socio para buscar", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<Prestamo> prestamos = prestamoDAO.findAll();
            List<Prestamo> prestamosEncontrados = prestamos.stream()
                    .filter(p -> p.getSocio() != null && p.getSocio().getNombre().toLowerCase().contains(nombreSocio.toLowerCase()))
                    .collect(Collectors.toList());
            
            if (prestamosEncontrados.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron préstamos para el socio: " + nombreSocio, Alert.AlertType.INFORMATION);
            } else {
                prestamoObservable = FXCollections.observableArrayList(prestamosEncontrados);
                prestamoTable.setItems(prestamoObservable);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario() {
        cbSocio.setValue(null);
        cbLibro.setValue(null);
        dpFechaPrestamo.setValue(null);
        dpFechaDevolucion.setValue(null);
        tfBuscar.clear();
        prestamoTable.getSelectionModel().clearSelection();
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
        Prestamo prestamoSeleccionado = prestamoTable.getSelectionModel().getSelectedItem();
        if (prestamoSeleccionado != null) {
            // Buscar el Socio en la lista observable
            Socio socioSeleccionado = sociosObservable.stream()
                    .filter(s -> s.getId().equals(prestamoSeleccionado.getSocio().getId()))
                    .findFirst()
                    .orElse(prestamoSeleccionado.getSocio());
            cbSocio.setValue(socioSeleccionado);
            
            // Buscar el Libro en la lista observable
            Libro libroSeleccionado = librosObservable.stream()
                    .filter(l -> l.getId().equals(prestamoSeleccionado.getLibro().getId()))
                    .findFirst()
                    .orElse(prestamoSeleccionado.getLibro());
            cbLibro.setValue(libroSeleccionado);
            
            dpFechaPrestamo.setValue(prestamoSeleccionado.getFecha_prestamo());
            dpFechaDevolucion.setValue(prestamoSeleccionado.getFecha_devolucion());
        }
    }
}
