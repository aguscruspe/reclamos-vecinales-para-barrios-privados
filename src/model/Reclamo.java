package model;

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
    private Vecino vecinoReporta;
    private EmpleadoMunicipal empleadoAsignado; // Puede ser null
    private List<HistorialEstado> historial;

    public Reclamo(TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecinoReporta) {
        this.id = ++contadorId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.vecinoReporta = vecinoReporta;
        this.historial = new ArrayList<>();

        // Aplicando GRASP Creator: El reclamo crea su estado inicial.
        this.agregarHistorial(EstadoReclamo.NUEVO, "Reclamo generado por el vecino.");
    }

    // Constructor para carga desde persistencia
    public Reclamo(int id, TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecinoReporta,
                   EmpleadoMunicipal empleadoAsignado, List<HistorialEstado> historial) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.vecinoReporta = vecinoReporta;
        this.empleadoAsignado = empleadoAsignado;
        this.historial = historial;
        if (id > contadorId)
            contadorId = id; // Actualizar contador estático
    }

    // Método privado para mantener la cohesión
    private void agregarHistorial(EstadoReclamo estado, String observacion) {
        HistorialEstado nuevoEstado = new HistorialEstado(estado, observacion);
        this.historial.add(nuevoEstado);
    }

    /**
     * GRASP - Information Expert:
     * El reclamo es el experto en saber cuál es su estado actual.
     */
    public EstadoReclamo getEstadoActual() {
        if (historial.isEmpty()) {
            return null; // No debería pasar
        }
        return historial.get(historial.size() - 1).getEstado();
    }

    public void asignarEmpleado(EmpleadoMunicipal empleado, String observacion) {
        this.empleadoAsignado = empleado;
        agregarHistorial(EstadoReclamo.ASIGNADO, observacion);
    }

    public void actualizarEstado(EstadoReclamo nuevoEstado, String observacion) {
        if (nuevoEstado != getEstadoActual()) {
            agregarHistorial(nuevoEstado, observacion);
        }
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

    public Vecino getVecinoReporta() {
        return vecinoReporta;
    }

    public EmpleadoMunicipal getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public List<HistorialEstado> getHistorial() {
        return new ArrayList<>(historial); // Devuelve copia
    }

    // --- INICIO DE LA CORRECCIÓN ---
    // Este es el método que faltaba y causaba el error
    /**
     * Devuelve la fecha de creación del reclamo, que es la fecha
     * del primer estado en el historial.
     * @return LocalDateTime la fecha de creación, o null si el historial está vacío.
     */
    public java.time.LocalDateTime getFechaCreacion() {
        if (historial != null && !historial.isEmpty()) {
            // El primer estado (índice 0) es el de creación
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
                vecinoReporta.getNombreCompleto(),
                asignado);
    }
}
