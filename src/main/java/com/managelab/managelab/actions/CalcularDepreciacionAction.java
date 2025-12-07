package com.managelab.managelab.actions;
import java.math.BigDecimal;
import java.util.Date;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import com.managelab.managelab.modelo.*;

/**
 * Accion para calcular y registrar depreciacion de un activo - CU 9
 * Genera un registro en la tabla DepreciacionActivo.
 */
public class CalcularDepreciacionAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        Long id = (Long) getView().getValue("id");

        if (id == null) {
            addError("Debe seleccionar un activo");
            return;
        }

        Activo activo = XPersistence.getManager().find(Activo.class, id);

        if (activo == null) {
            addError("Activo no encontrado");
            return;
        }

        if (activo.getCostoInicial() == null || activo.getVidaUtilAnios() == null
                || activo.getVidaUtilAnios() == 0 || activo.getFechaAdquisicion() == null) {
            addError("El activo debe tener costo inicial, vida util y fecha de adquisicion");
            return;
        }

        // Crear registro de depreciacion
        DepreciacionActivo depreciacion = new DepreciacionActivo();
        depreciacion.setActivo(activo);
        depreciacion.setFechaCalculo(new Date());

        // Calcular valor actual
        long diasTranscurridos = (new Date().getTime() - activo.getFechaAdquisicion().getTime())
                / (1000L * 60 * 60 * 24);
        long aniosTranscurridos = diasTranscurridos / 365;

        if (aniosTranscurridos < 0)
            aniosTranscurridos = 0;
        if (aniosTranscurridos > activo.getVidaUtilAnios())
            aniosTranscurridos = activo.getVidaUtilAnios();

        BigDecimal depreciacionAnual = activo.getCostoInicial()
                .divide(new BigDecimal(activo.getVidaUtilAnios()), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal depreciacionTotal = depreciacionAnual.multiply(new BigDecimal(aniosTranscurridos));
        BigDecimal valorActual = activo.getCostoInicial().subtract(depreciacionTotal);

        depreciacion.setValorCalculado(valorActual);

        XPersistence.getManager().persist(depreciacion);
        XPersistence.commit();

        addMessage("Depreciacion calculada: " + valorActual + " (Activo: " + activo.getNombre() + ")");
    }
}
