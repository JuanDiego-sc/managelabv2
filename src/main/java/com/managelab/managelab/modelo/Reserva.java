package com.managelab.managelab.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.managelab.managelab.calculators.*;

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

    /**
     * Required + ReadOnly necesita DefaultValueCalculator para que al dar "New"
     * se cargue el valor en pantalla.
     */
    @Required
    @Enumerated(EnumType.STRING)
    @ReadOnly
    @DefaultValueCalculator(EstadoReservaPendienteCalculator.class)
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    @Column(length = 500)
    @Stereotype("MEMO")
    private String motivoRechazo;

    /**
     * Flag para evitar validaciones "pesadas" (con query) cuando se hace merge()
     * desde acciones de aprobar/rechazar.
     */
    @Transient
    private boolean omitirValidacionDisponibilidad = false;

    /**
     * UN SOLO callback para persist/update (evita el error de multiples @PrePersist).
     */
    @PrePersist
    @PreUpdate
    private void antesDeGuardar() {
        asegurarEstadoInicial();

        // Si viene de Aprobar/Rechazar (acciones), evitamos la validacion con query
        if (!omitirValidacionDisponibilidad) {
            validarDisponibilidadInterna();
        }
    }

    private void asegurarEstadoInicial() {
        if (estadoReserva == null) {
            estadoReserva = EstadoReserva.PENDIENTE;
        }
    }

    private void validarDisponibilidadInterna() {
        if (laboratorio == null || laboratorio.getId() == null || fecha == null || horaInicio == null || horaFin == null) {
            return;
        }

        // Si está rechazada, no afecta disponibilidad
        if (estadoReserva == EstadoReserva.RECHAZADA) return;

        int inicio = toMinutesOrFail(horaInicio, "horaInicio");
        int fin = toMinutesOrFail(horaFin, "horaFin");

        if (fin <= inicio) {
            throw new javax.validation.ValidationException("La hora fin debe ser mayor a la hora inicio");
        }

        String q = "SELECT r FROM Reserva r " +
                   "WHERE r.laboratorio.id = :labId " +
                   "AND r.fecha = :fecha " +
                   "AND r.estadoReserva = :estadoAprobada " +
                   "AND (:idActual IS NULL OR r.id <> :idActual)";

        @SuppressWarnings("unchecked")
        List<Reserva> aprobadas = XPersistence.getManager()
            .createQuery(q)
            .setParameter("labId", laboratorio.getId())
            .setParameter("fecha", fecha)
            .setParameter("estadoAprobada", EstadoReserva.APROBADA)
            .setParameter("idActual", id)
            .getResultList();

        for (Reserva r : aprobadas) {
            if (r.getHoraInicio() == null || r.getHoraFin() == null) continue;

            int rInicio = toMinutesOrFail(r.getHoraInicio(), "horaInicio existente");
            int rFin = toMinutesOrFail(r.getHoraFin(), "horaFin existente");

            boolean traslapa = (rInicio < fin) && (rFin > inicio);
            if (traslapa) {
                throw new javax.validation.ValidationException(
                    "El laboratorio ya tiene una reserva aprobada en ese horario"
                );
            }
        }

        // Normaliza a HH:mm
        this.horaInicio = formatHHmm(inicio);
        this.horaFin = formatHHmm(fin);
    }

    private int toMinutesOrFail(String h, String campo) {
        try {
            String v = h.trim();
            String[] parts = v.split(":");
            if (parts.length != 2) throw new IllegalArgumentException();
            int hh = Integer.parseInt(parts[0]);
            int mm = Integer.parseInt(parts[1]);
            if (hh < 0 || hh > 23 || mm < 0 || mm > 59) throw new IllegalArgumentException();
            return hh * 60 + mm;
        } catch (Exception ex) {
            throw new javax.validation.ValidationException(
                "Formato invalido en " + campo + ". Use HH:mm (ej: 09:30)"
            );
        }
    }

    private String formatHHmm(int minutes) {
        int hh = minutes / 60;
        int mm = minutes % 60;
        return String.format("%02d:%02d", hh, mm);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Laboratorio getLaboratorio() { return laboratorio; }
    public void setLaboratorio(Laboratorio laboratorio) { this.laboratorio = laboratorio; }

    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public EstadoReserva getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(EstadoReserva estadoReserva) { this.estadoReserva = estadoReserva; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public boolean isOmitirValidacionDisponibilidad() { return omitirValidacionDisponibilidad; }
    public void setOmitirValidacionDisponibilidad(boolean omitirValidacionDisponibilidad) {
        this.omitirValidacionDisponibilidad = omitirValidacionDisponibilidad;
    }
}
