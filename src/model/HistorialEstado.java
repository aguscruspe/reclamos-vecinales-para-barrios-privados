package model;

import model.enums.EstadoReclamo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialEstado {
    private LocalDateTime fecha;
    private EstadoReclamo estado;
    private String observacion;

    public HistorialEstado(EstadoReclamo estado, String observacion) {
        this.fecha = LocalDateTime.now();
        this.estado = estado;
        this.observacion = observacion;
    }

    public HistorialEstado(LocalDateTime fecha, EstadoReclamo estado, String observacion) {
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion;
    }

    public EstadoReclamo getEstado() {
        return estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s",
                fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                estado,
                observacion);
    }
}