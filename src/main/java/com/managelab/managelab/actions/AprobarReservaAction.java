package com.managelab.managelab.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import com.managelab.managelab.modelo.*;

/**
 * Accion para aprobar una reserva - CU 7
 * Cambia el estado de la reserva a APROBADA.
 */
public class AprobarReservaAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        Long id = (Long) getView().getValue("id");

        if (id == null) {
            addError("Debe seleccionar una reserva");
            return;
        }

        Reserva reserva = XPersistence.getManager().find(Reserva.class, id);

        if (reserva == null) {
            addError("Reserva no encontrada");
            return;
        }

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            addError("Solo se pueden aprobar reservas pendientes");
            return;
        }

        reserva.setEstadoReserva(EstadoReserva.APROBADA);
        XPersistence.getManager().merge(reserva);
        XPersistence.commit();

        addMessage("Reserva aprobada exitosamente");
        getView().refresh();
    }
}
