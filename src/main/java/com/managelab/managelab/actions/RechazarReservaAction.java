package com.managelab.managelab.actions;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import com.managelab.managelab.modelo.*;

/**
 * Accion para rechazar una reserva - CU 7
 * Cambia el estado de la reserva a RECHAZADA y solicita un motivo.
 */
public class RechazarReservaAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        Long id = (Long) getView().getValue("id");
        String motivoRechazo = (String) getView().getValue("motivoRechazo");

        if (id == null) {
            addError("Debe seleccionar una reserva");
            return;
        }

        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            addError("Debe ingresar un motivo de rechazo");
            return;
        }

        Reserva reserva = XPersistence.getManager().find(Reserva.class, id);

        if (reserva == null) {
            addError("Reserva no encontrada");
            return;
        }

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            addError("Solo se pueden rechazar reservas pendientes");
            return;
        }

        reserva.setEstadoReserva(EstadoReserva.RECHAZADA);
        reserva.setMotivoRechazo(motivoRechazo);
        XPersistence.getManager().merge(reserva);
        XPersistence.commit();

        addMessage("Reserva rechazada");
        getView().refresh();
    }
}
