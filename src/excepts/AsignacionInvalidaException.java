package excepts;

/**
 * Se lanza cuando se intenta asignar un reclamo a un empleado
 * de un área que no corresponde al tipo de reclamo.
 */
public class AsignacionInvalidaException extends Exception {
    public AsignacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
