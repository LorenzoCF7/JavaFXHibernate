package org.example.javafxhibernate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.DAO.AutorDAO;
import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.Entities.Autor;
import org.example.Entities.Libro;
import org.example.Entities.Prestamo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibroController {
    @FXML
    private TableView<Libro> libroTable;
    
    @FXML
    private TableColumn<Libro, Integer> colId;
    
    @FXML
    private TableColumn<Libro, String> colTitulo;
    
    @FXML
    private TableColumn<Libro, String> colAutor;
    
    @FXML
    private TableColumn<Libro, String> colEditorial;
    
    @FXML
    private TableColumn<Libro, Integer> colAnyo;
    
    @FXML
    private TextField tfTitulo;
    
    @FXML
    private ComboBox<Autor> cbAutor;
    
    @FXML
    private TextField tfEditorial;
    
    @FXML
    private TextField tfAnyo;
    
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
    
    @FXML
    private Button btnLibrosSinPrestar;
    
    private LibroDAO libroDAO;
    private AutorDAO autorDAO;
    private PrestamoDAO prestamoDAO;
    private ObservableList<Libro> librosObservable;
    private ObservableList<Autor> autoresObservable;

    @FXML
    public void initialize() {
        libroDAO = new LibroDAO();
        autorDAO = new AutorDAO();
        prestamoDAO = new PrestamoDAO();
        setupTableColumns();
        loadAutoresComboBox();
        loadAllLibros();
        setupButtonHandlers();
        setupAnyoValidation();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(cellData -> {
            Autor autor = cellData.getValue().getAutor();
            return new javafx.beans.property.SimpleStringProperty(autor != null ? autor.getNombre() : "");
        });
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colAnyo.setCellValueFactory(new PropertyValueFactory<>("anyo_publicacion"));
        
        libroTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void loadAutoresComboBox() {
        List<Autor> autores = autorDAO.findAll();
        autoresObservable = FXCollections.observableArrayList(autores);
        cbAutor.setItems(autoresObservable);
        
        cbAutor.setCellFactory(param -> new ListCell<Autor>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre() + " (" + item.getNacionalidad() + ")");
            }
        });
        
        cbAutor.setButtonCell(new ListCell<Autor>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });
    }

    private void loadAllLibros() {
        List<Libro> libros = libroDAO.findAll();
        librosObservable = FXCollections.observableArrayList(libros);
        libroTable.setItems(librosObservable);
    }

    private void setupButtonHandlers() {
        btnAgregar.setOnAction(e -> agregarLibro());
        btnModificar.setOnAction(e -> modificarLibro());
        btnEliminar.setOnAction(e -> eliminarLibro());
        btnBuscar.setOnAction(e -> buscarLibro());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnListarTodos.setOnAction(e -> loadAllLibros());
        btnLibrosSinPrestar.setOnAction(e -> librosSinPrestar());
    }

    private void setupAnyoValidation() {
        tfAnyo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfAnyo.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void agregarLibro() {
        String titulo = tfTitulo.getText().trim();
        String editorial = tfEditorial.getText().trim();
        Autor autor = cbAutor.getValue();
        String anyoText = tfAnyo.getText().trim();

        if (titulo.isEmpty() || editorial.isEmpty() || autor == null || anyoText.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            int anyo = Integer.parseInt(anyoText);
            Libro nuevoLibro = new Libro(titulo, editorial, autor, anyo);
            libroDAO.create(nuevoLibro);
            mostrarAlerta("Éxito", "Libro agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllLibros();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El año debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el libro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarLibro() {
        Libro libroSeleccionado = libroTable.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un libro de la tabla", Alert.AlertType.ERROR);
            return;
        }

        String titulo = tfTitulo.getText().trim();
        String editorial = tfEditorial.getText().trim();
        Autor autor = cbAutor.getValue();
        String anyoText = tfAnyo.getText().trim();

        if (titulo.isEmpty() || editorial.isEmpty() || autor == null || anyoText.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            int anyo = Integer.parseInt(anyoText);
            libroSeleccionado.setTitulo(titulo);
            libroSeleccionado.setEditorial(editorial);
            libroSeleccionado.setAutor(autor);
            libroSeleccionado.setAnyo_publicacion(anyo);
            libroDAO.update(libroSeleccionado);
            mostrarAlerta("Éxito", "Libro modificado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            loadAllLibros();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El año debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo modificar el libro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarLibro() {
        Libro libroSeleccionado = libroTable.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta("Error", "Por favor selecciona un libro de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este libro?");
        confirmacion.setContentText("Se eliminará: " + libroSeleccionado.getTitulo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                libroDAO.deleteById(libroSeleccionado.getId());
                mostrarAlerta("Éxito", "Libro eliminado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                loadAllLibros();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el libro: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void buscarLibro() {
        String tituloBusqueda = tfBuscar.getText().trim();

        if (tituloBusqueda.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa un título para buscar", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<Libro> libros = libroDAO.findByTitulo(tituloBusqueda);
            if (libros.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron libros con ese título", Alert.AlertType.INFORMATION);
            } else {
                librosObservable = FXCollections.observableArrayList(libros);
                libroTable.setItems(librosObservable);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarFormulario() {
        tfTitulo.clear();
        tfEditorial.clear();
        tfAnyo.clear();
        tfBuscar.clear();
        cbAutor.setValue(null);
        libroTable.getSelectionModel().clearSelection();
    }

    private void cargarDatosSeleccionados() {
        Libro libroSeleccionado = libroTable.getSelectionModel().getSelectedItem();
        if (libroSeleccionado != null) {
            tfTitulo.setText(libroSeleccionado.getTitulo());
            tfEditorial.setText(libroSeleccionado.getEditorial());
            tfAnyo.setText(String.valueOf(libroSeleccionado.getAnyo_publicacion()));
            cbAutor.setValue(libroSeleccionado.getAutor());
        }
    }

    @FXML
    private void librosSinPrestar() {
        try {
            List<Libro> todosLibros = libroDAO.findAll();
            List<Prestamo> prestamos = prestamoDAO.findAll();
            
            // Obtener los IDs de libros que tienen al menos un prestamo
            List<Integer> librosConPrestamo = prestamos.stream()
                    .map(p -> p.getLibro().getId())
                    .collect(Collectors.toList());
            
            // Filtrar libros que NO estan en la lista de libros prestados
            List<Libro> librosSinPrestarList = todosLibros.stream()
                    .filter(l -> !librosConPrestamo.contains(l.getId()))
                    .collect(Collectors.toList());
            
            if (librosSinPrestarList.isEmpty()) {
                mostrarAlerta("Información", "Todos los libros han sido prestados", Alert.AlertType.INFORMATION);
                loadAllLibros();
            } else {
                librosObservable = FXCollections.observableArrayList(librosSinPrestarList);
                libroTable.setItems(librosObservable);
                mostrarAlerta("Información", "Se encontraron " + librosSinPrestarList.size() + " libros sin prestar", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar libros sin prestar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
