package com.managelab.managelab.modelo;

import java.util.Date;
import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.jpa.XPersistence;

/**
 * Entidad Reserva - CU 5, 6, 7: Solicitud y Aprobacion de Reservas
 * El estado inicial es PENDIENTE.
 * Incluye validacion de disponibilidad (no traslape con reservas aprobadas).
 */
@Entity
@View(members = "laboratorio; solicitante; fecha; horaInicio, horaFin; estadoReserva; motivoRechazo")
@Tab(properties = "laboratorio.nombre, solicitante.nombre, fecha, horaInicio, horaFin, estadoReserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Required
    @DescriptionsList(descriptionProperties = "nombre")
    private Laboratorio laboratorio;

    @ManyToOne
    @Required
    @DescriptionsList(descriptionProperties = "nombre")
    private Usuario solicitante;

    @Required
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Required
    @Stereotype("TIME")
    private String horaInicio;

    @Required
    @Stereotype("TIME")
    private String horaFin;

    @Required
    @Enumerated(EnumType.STRING)
    @ReadOnly
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    @Column(length = 500)
    @Stereotype("MEMO")
    private String motivoRechazo;

    /**
     * Validacion antes de persistir: verificar disponibilidad
     */
    @PrePersist
    @PreUpdate
    public void validarDisponibilidad() {
        if (laboratorio == null || fecha == null || horaInicio == null || horaFin == null) {
            return;
        }

        // Solo validar si la reserva no esta rechazada
        if (estadoReserva == EstadoReserva.RECHAZADA) {
            return;
        }

        String query = "SELECT COUNT(r) FROM Reserva r WHERE r.laboratorio.id = :labId " +
                "AND r.fecha = :fecha " +
                "AND r.estadoReserva = :estadoAprobada " +
                "AND r.id != :reservaId " +
                "AND ((r.horaInicio <= :horaInicio AND r.horaFin > :horaInicio) " +
                "OR (r.horaInicio < :horaFin AND r.horaFin >= :horaFin) " +
                "OR (r.horaInicio >= :horaInicio AND r.horaFin <= :horaFin))";

        Long count = (Long) XPersistence.getManager()
                .createQuery(query)
                .setParameter("labId", laboratorio.getId())
                .setParameter("fecha", fecha)
                .setParameter("estadoAprobada", EstadoReserva.APROBADA)
                .setParameter("reservaId", id != null ? id : -1L)
                .setParameter("horaInicio", horaInicio)
                .setParameter("horaFin", horaFin)
                .getSingleResult();

        if (count > 0) {
            throw new javax.validation.ValidationException(
                    "El laboratorio ya tiene una reserva aprobada en ese horario");
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
