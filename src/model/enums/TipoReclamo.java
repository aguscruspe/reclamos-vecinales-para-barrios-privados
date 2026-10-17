package model.enums;

public enum TipoReclamo {
    ACCESO_NO_AUTORIZADO(AreaEmpleado.SEGURIDAD),
    VELOCIDAD_EXCESIVA(AreaEmpleado.SEGURIDAD),
    PLOMERIA(AreaEmpleado.MANTENIMIENTO),
    ELECTRICIDAD(AreaEmpleado.MANTENIMIENTO),
    CORTE_DE_PASTO(AreaEmpleado.JARDINERIA),
    PODA(AreaEmpleado.JARDINERIA),
    EXPENSAS(AreaEmpleado.ADMINISTRACION),
    CONVIVENCIA(AreaEmpleado.ADMINISTRACION),
    OTRO(AreaEmpleado.ADMINISTRACION);

    private final AreaEmpleado area;

    TipoReclamo(AreaEmpleado area) {
        this.area = area;
    }

    public AreaEmpleado getArea() {
        return area;
    }
}
