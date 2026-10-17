package model;

import model.enums.AreaEmpleado;

public class Empleado extends Usuario {

    private AreaEmpleado area;

    public Empleado(String nombreUsuario, String password, String nombreCompleto, String dni,
            AreaEmpleado area) {
        super(nombreUsuario, password, nombreCompleto, dni);
        this.area = area;
    }

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
}