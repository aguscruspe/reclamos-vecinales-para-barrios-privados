package model;

import model.enums.SectorEmpleado;
import excepts.UsuarioNoValidoException;

public class EmpleadoMunicipal extends Usuario {

    private SectorEmpleado sector;

    public EmpleadoMunicipal(String username, String password, String nombreCompleto, String dni,
            SectorEmpleado sector) {
        super(username, password, nombreCompleto, dni);
        this.sector = sector;
    }

    // Constructor para carga desde persistencia
    public EmpleadoMunicipal(int id, String username, String password, String nombreCompleto, String dni,
            SectorEmpleado sector) {
        super(id, username, password, nombreCompleto, dni);
        this.sector = sector;
    }

    public SectorEmpleado getSector() {
        return sector;
    }

    @Override
    public String getTipoUsuario() {
        return "EmpleadoMunicipal";
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                sector == null) {
            throw new UsuarioNoValidoException("Datos incompletos para el empleado.");
        }
    }
}