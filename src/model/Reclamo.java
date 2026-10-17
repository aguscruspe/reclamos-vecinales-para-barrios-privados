package model;

import excepts.AsignacionInvalidaException;
import model.enums.EstadoReclamo;
import model.enums.TipoReclamo;
import java.util.ArrayList;
import java.util.List;

/**
 * GRASP - Information Expert:
 * Esta clase es la experta en la información de un reclamo.
 * Sabe quién lo reportó, quién está asignado y su historial de estados.
 *
 * Métodos como getEstadoActual() demuestran este principio.
 *
 * GRASP - Creator:
 * Esta clase es la creadora de sus HistorialEstado.
 * Cuando se crea un reclamo o se actualiza su estado, esta clase
 * instancia y agrega el nuevo HistorialEstado a su lista interna.
 */
public class Reclamo {

    private static int contadorId = 0;
    private int id;
    private TipoReclamo tipo;
    private String descripcion;
    private String ubicacion;

    // Relaciones clave
    private Vecino vecino;
    private Empleado empleadoAsignado;
    private List<HistorialEstado> historial;

    public Reclamo(TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecino) {
        this.id = ++contadorId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.vecino = vecino;
        this.historial = new ArrayList<>();

        this.agregarHistorial(EstadoReclamo.NUEVO, "Reclamo generado por el vecino.");
    }

    public Reclamo(int id, TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecino,
                   Empleado empleadoAsignado, List<HistorialEstado> historial) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.vecino = vecino;
        this.empleadoAsignado = empleadoAsignado;
        this.historial = historial;
        if (id > contadorId)
            contadorId = id;
    }

    private void agregarHistorial(EstadoReclamo estado, String observacion) {
        HistorialEstado nuevoEstado = new HistorialEstado(estado, observacion);
        this.historial.add(nuevoEstado);
    }

    public EstadoReclamo getEstadoActual() {
        if (historial.isEmpty()) {
            return null; // No debería pasar
        }
        return historial.get(historial.size() - 1).getEstado();
    }

    public void asignarEmpleado(Empleado empleado, String observacion) throws AsignacionInvalidaException {
        if (empleado.getArea() != tipo.getArea()) {
            throw new AsignacionInvalidaException("Un reclamo de tipo " + tipo + " corresponde al área "
                    + tipo.getArea() + ", pero el empleado es del área " + empleado.getArea() + ".");
        }
        this.empleadoAsignado = empleado;
        agregarHistorial(EstadoReclamo.ASIGNADO, observacion);
    }

    public void actualizarEstado(EstadoReclamo nuevoEstado, String observacion) {
        if (nuevoEstado != getEstadoActual()) {
            agregarHistorial(nuevoEstado, observacion);
        }
    }

    public boolean estaAsignadoA(Usuario usuario) {
        return empleadoAsignado != null && empleadoAsignado.getId() == usuario.getId();
    }

    public boolean fueHechoPor(Vecino vecino) {
        return this.vecino.getId() == vecino.getId();
    }
    
    // Getters
    public int getId() {
        return id;
    }

    public TipoReclamo getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public Vecino getVecino() {
        return vecino;
    }

    public Empleado getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public List<HistorialEstado> getHistorial() {
        return new ArrayList<>(historial);
    }

    public java.time.LocalDateTime getFechaCreacion() {
        if (historial != null && !historial.isEmpty()) {
            return historial.get(0).getFecha();
        }
        return null;
    }

    @Override
    public String toString() {
        String asignado = (empleadoAsignado != null) ? empleadoAsignado.getNombreCompleto() : "N/A";
        return String.format("Reclamo ID: %d [%s] %s (%s) - Reporta: %s - Asignado a: %s",
                id,
                getEstadoActual(),
                tipo,
                descripcion,
                vecino.getNombreCompleto(),
                asignado);
    }
}