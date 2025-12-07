package com.managelab.managelab.actions;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import com.managelab.managelab.modelo.*;

/**
 * Accion para inactivar un laboratorio en lugar de eliminarlo - CU 1
 * Implementa soft delete segun requisito RF.
 */
public class InactivarLaboratorioAction extends ViewBaseAction {

    @Override
    public void execute() throws Exception {
        Long id = (Long) getView().getValue("id");

        if (id == null) {
            addError("Debe seleccionar un laboratorio");
            return;
        }

        Laboratorio laboratorio = XPersistence.getManager().find(Laboratorio.class, id);

        if (laboratorio == null) {
            addError("Laboratorio no encontrado");
            return;
        }

        laboratorio.setEstado(EstadoLaboratorio.INACTIVO);
        XPersistence.getManager().merge(laboratorio);
        XPersistence.commit();

        addMessage("Laboratorio inactivado exitosamente");
        getView().refresh();
    }
}
