package org.example.Entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    // RELACIÓN MANY-TO-ONE (Muchos libros -> un autor)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor; // Este es el campo referenciado en mappedBy de Autor

    private String editorial;

    private int anyo_publicacion;

    // ⭐️ RELACIÓN ONE-TO-MANY (Un libro -> muchos prestamos)
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Prestamo> prestamos = new HashSet<>();

    public Libro() {}

    public Libro(String titulo, String editorial, Autor autor, int anyo_publicacion) {
        this.titulo = titulo;
        this.editorial = editorial;
        this.autor = autor;
        this.anyo_publicacion = anyo_publicacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnyo_publicacion() {
        return anyo_publicacion;
    }

    public void setAnyo_publicacion(int anyo_publicacion) {
        this.anyo_publicacion = anyo_publicacion;
    }

    public Set<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }
}
