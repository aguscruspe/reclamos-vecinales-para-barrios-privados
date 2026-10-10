package model;

import model.enums.AreaEmpleado;
import excepts.UsuarioNoValidoException;

public class Empleado extends Usuario {

    private AreaEmpleado area;

    public Empleado(String nombreUsuario, String password, String nombreCompleto, String dni,
            AreaEmpleado area) {
        super(nombreUsuario, password, nombreCompleto, dni);
        this.area = area;
    }

    // Constructor para carga desde persistencia
    public Empleado(int id, String nombreUsuario, String password, String nombreCompleto, String dni,
            AreaEmpleado area) {
        super(id, nombreUsuario, password, nombreCompleto, dni);
        this.area = area;
    }

    public AreaEmpleado getArea() {
        return area;
    }

    @Override
    public String getTipoUsuario() {
        return "Empleado";
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                area == null) {
            throw new UsuarioNoValidoException("Datos incompletos para el empleado.");
        }
    }
}