package com.managelab.managelab.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Entidad Usuario - CU 8: Gestion de Usuarios
 * Solo el administrador puede crear usuarios y asignar roles.
 * El correo electronico debe ser unico.
 */
@Entity
@View(members = "nombre; email; password; rol; activo")
@Tab(properties = "nombre, email, rol, activo")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Required
    @Column(length = 100)
    private String nombre;

    @Required
    @Column(length = 100, unique = true)
    @Stereotype("EMAIL")
    private String email;

    @Required
    @Column(length = 100)
    @Stereotype("PASSWORD")
    private String password;

    @Required
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean activo = true;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
