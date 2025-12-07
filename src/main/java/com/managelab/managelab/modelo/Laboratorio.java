package com.managelab.managelab.modelo;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Entidad Laboratorio - CU 1: Gestion de Laboratorios
 * CU 2: Asignacion de Responsable
 * Implementa soft delete: inactivar en lugar de borrar.
 */
@Entity
@View(members = "codigo; nombre; ubicacion; descripcion; estado; responsable; activos")
@Tab(properties = "codigo, nombre, ubicacion, estado, responsable.nombre")
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Required
    @Column(length = 20, unique = true)
    private String codigo;

    @Required
    @Column(length = 100)
    private String nombre;

    @Required
    @Column(length = 200)
    private String ubicacion;

    @Column(length = 500)
    @Stereotype("MEMO")
    private String descripcion;

    @Required
    @Enumerated(EnumType.STRING)
    private EstadoLaboratorio estado = EstadoLaboratorio.ACTIVO;

    /**
     * Responsable del laboratorio - solo usuarios con rol RESPONSABLE_LABORATORIO
     * Un laboratorio tiene maximo un responsable activo
     */
    @ManyToOne
    @DescriptionsList(descriptionProperties = "nombre")
    private Usuario responsable;

    @OneToMany(mappedBy = "laboratorio", cascade = CascadeType.ALL)
    @ListProperties("codigoInventario, nombre, categoria.nombre, costoInicial")
    private List<Activo> activos = new ArrayList<>();

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoLaboratorio getEstado() {
        return estado;
    }

    public void setEstado(EstadoLaboratorio estado) {
        this.estado = estado;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public List<Activo> getActivos() {
        return activos;
    }

    public void setActivos(List<Activo> activos) {
        this.activos = activos;
    }
}
