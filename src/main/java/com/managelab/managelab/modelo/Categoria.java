package com.managelab.managelab.modelo;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Entidad Categoria - CU 4: Categorizacion
 * Valida antes de borrar que no tenga activos asignados.
 */
@Entity
@View(members = "nombre; descripcion")
@Tab(properties = "nombre, descripcion")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Required
    @Column(length = 100)
    private String nombre;

    @Column(length = 500)
    @Stereotype("MEMO")
    private String descripcion;

    @OneToMany(mappedBy = "categoria")
    private List<Activo> activos = new ArrayList<>();

    /**
     * Validacion: No permitir eliminar si tiene activos asignados
     */
    @PreRemove
    public void validarEliminacion() {
        if (activos != null && !activos.isEmpty()) {
            throw new javax.validation.ValidationException(
                    "No se puede eliminar la categoria porque tiene activos asignados");
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Activo> getActivos() {
        return activos;
    }

    public void setActivos(List<Activo> activos) {
        this.activos = activos;
    }
}
