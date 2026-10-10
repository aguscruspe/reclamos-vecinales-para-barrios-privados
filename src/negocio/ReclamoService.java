package negocio;

import excepts.ReclamoNoEncontradoException;
import excepts.UsuarioNoValidoException;
import model.Empleado;
import model.Reclamo;
import model.Usuario;
import model.Vecino;
import model.enums.EstadoReclamo;
import model.enums.AreaEmpleado;
import model.enums.TipoReclamo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SRP: Esta clase tiene la única responsabilidad de gestionar la lógica
 * de negocio relacionada con los Reclamos.
 */
public class ReclamoService {

    private Map<Integer, Reclamo> reclamos; // Referencia al mapa principal
    private Map<String, Usuario> usuarios; // Necesaria para buscar empleados

    public ReclamoService(Map<Integer, Reclamo> reclamos, Map<String, Usuario> usuarios) {
        this.reclamos = reclamos;
        this.usuarios = usuarios;
    }

    public Reclamo crearReclamo(TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecino) {
        // Delegamos la creación al constructor de Reclamo (GRASP Creator)
        Reclamo nuevoReclamo = new Reclamo(tipo, descripcion, ubicacion, vecino);

        // Añadir al estado en memoria
        this.reclamos.put(nuevoReclamo.getId(), nuevoReclamo);
        return nuevoReclamo;
    }

    public void asignarReclamo(int idReclamo, String usernameEmpleado, String adminUsername)
            throws ReclamoNoEncontradoException, UsuarioNoValidoException {

        Reclamo reclamo = reclamos.get(idReclamo);
        if (reclamo == null) {
            throw new ReclamoNoEncontradoException("No se encontró el reclamo con ID: " + idReclamo);
        }

        Usuario u = usuarios.get(usernameEmpleado);
        if (u == null || !(u instanceof Empleado)) {
            throw new UsuarioNoValidoException("No se encontró un empleado con el nombreUsuario: " + usernameEmpleado);
        }

        Empleado empleado = (Empleado) u;

        // Delegamos la lógica al experto (Information Expert)
        reclamo.asignarEmpleado(empleado, "Asignado por " + adminUsername);
    }

    public void actualizarEstadoReclamo(int idReclamo, EstadoReclamo nuevoEstado, String observacion, Usuario usuarioQueActualiza)
            throws ReclamoNoEncontradoException, UsuarioNoValidoException {

        Reclamo reclamo = reclamos.get(idReclamo);
        if (reclamo == null) {
            throw new ReclamoNoEncontradoException("No se encontró el reclamo con ID: " + idReclamo);
        }

        // Si es un empleado, solo puede gestionar sus reclamos asignados
        if (usuarioQueActualiza instanceof Empleado) {
            if (reclamo.getEmpleadoAsignado() == null
                    || reclamo.getEmpleadoAsignado().getId() != usuarioQueActualiza.getId()) {
                throw new UsuarioNoValidoException("Este reclamo no está asignado a usted.");
            }
        }

        // Delegamos al experto
        reclamo.actualizarEstado(nuevoEstado,
                observacion + " (Actualizado por: " + usuarioQueActualiza.getNombreUsuario() + ")");
    }

    // --- Métodos de Consulta ---

    public List<Reclamo> getTodosLosReclamos() {
        return new ArrayList<>(reclamos.values());
    }

    public List<Reclamo> getReclamosPorVecino(Vecino vecino) {
        return reclamos.values().stream()
                .filter(r -> r.getVecino().getId() == vecino.getId())
                .collect(Collectors.toList());
    }

    public List<Reclamo> getReclamosPorEmpleado(Empleado empleado) {
        return reclamos.values().stream()
                .filter(r -> r.getEmpleadoAsignado() != null && r.getEmpleadoAsignado().getId() == empleado.getId())
                .collect(Collectors.toList());
    }

    public List<Reclamo> getTodosLosReclamosOrdenadosPorFecha() {
        return reclamos.values().stream()
                .sorted(Comparator.comparing(Reclamo::getFechaCreacion).reversed())
                .collect(Collectors.toList());
    }

    public Set<AreaEmpleado> getAreasConReclamosAsignados() {
        return reclamos.values().stream()
                .filter(r -> r.getEmpleadoAsignado() != null)
                .map(r -> r.getEmpleadoAsignado().getArea())
                .collect(Collectors.toSet());
    }
}