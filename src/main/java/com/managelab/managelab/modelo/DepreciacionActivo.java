package com.managelab.managelab.modelo;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Entidad DepreciacionActivo - CU 9: Calculo de Depreciacion
 * Registro historico de depreciacion para un activo.
 * Formula: Metodo lineal simple.
 */
@Entity
@View(members = "activo; fechaCalculo; valorCalculado")
@Tab(properties = "activo.codigoInventario, activo.nombre, fechaCalculo, valorCalculado")
public class DepreciacionActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Required
    @DescriptionsList(descriptionProperties = "nombre, codigoInventario")
    private Activo activo;

    @Required
    @Temporal(TemporalType.DATE)
    private Date fechaCalculo;

    @Required
    @Stereotype("MONEY")
    private BigDecimal valorCalculado;

    /**
     * Calcula la depreciacion antes de persistir
     */
    @PrePersist
    public void calcularDepreciacion() {
        if (activo != null && fechaCalculo == null) {
            fechaCalculo = new Date();
        }

        if (activo != null && activo.getCostoInicial() != null && activo.getVidaUtilAnios() != null
                && activo.getVidaUtilAnios() > 0 && activo.getFechaAdquisicion() != null) {

            long diasTranscurridos = (fechaCalculo.getTime() - activo.getFechaAdquisicion().getTime())
                    / (1000L * 60 * 60 * 24);
            long aniosTranscurridos = diasTranscurridos / 365;

            if (aniosTranscurridos < 0)
                aniosTranscurridos = 0;
            if (aniosTranscurridos > activo.getVidaUtilAnios())
                aniosTranscurridos = activo.getVidaUtilAnios();

            BigDecimal depreciacionAnual = activo.getCostoInicial()
                    .divide(new BigDecimal(activo.getVidaUtilAnios()), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal depreciacionTotal = depreciacionAnual.multiply(new BigDecimal(aniosTranscurridos));

            valorCalculado = activo.getCostoInicial().subtract(depreciacionTotal);
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activo getActivo() {
        return activo;
    }

    public void setActivo(Activo activo) {
        this.activo = activo;
    }

    public Date getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(Date fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }
}
