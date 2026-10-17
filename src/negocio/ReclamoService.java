package negocio;

import excepts.AsignacionInvalidaException;
import excepts.ReclamoNoEncontradoException;
import excepts.UsuarioNoValidoException;
import model.Empleado;
import model.Reclamo;
import model.Usuario;
import model.Vecino;
import model.enums.EstadoReclamo;
import model.enums.TipoReclamo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReclamoService {

    private Map<Integer, Reclamo> reclamos;
    private Map<String, Usuario> usuarios; 

    public ReclamoService(Map<Integer, Reclamo> reclamos, Map<String, Usuario> usuarios) {
        this.reclamos = reclamos;
        this.usuarios = usuarios;
    }

    public Reclamo crearReclamo(TipoReclamo tipo, String descripcion, String ubicacion, Vecino vecino) {
        Reclamo nuevoReclamo = new Reclamo(tipo, descripcion, ubicacion, vecino);

        this.reclamos.put(nuevoReclamo.getId(), nuevoReclamo);
        return nuevoReclamo;
    }

    public void asignarReclamo(int idReclamo, String usernameEmpleado, String usernameIntendente)
            throws ReclamoNoEncontradoException, UsuarioNoValidoException, AsignacionInvalidaException {

        Reclamo reclamo = reclamos.get(idReclamo);
        if (reclamo == null) {
            throw new ReclamoNoEncontradoException("No se encontró el reclamo con ID: " + idReclamo);
        }

        Usuario usuarioEncontrado = usuarios.get(usernameEmpleado);
        if (usuarioEncontrado == null || !(usuarioEncontrado instanceof Empleado)) {
            throw new UsuarioNoValidoException("No se encontró un empleado con el nombreUsuario: " + usernameEmpleado);
        }

        Empleado empleado = (Empleado) usuarioEncontrado;

        reclamo.asignarEmpleado(empleado, "Asignado por " + usernameIntendente);
    }

    public void actualizarEstadoReclamo(int idReclamo, EstadoReclamo nuevoEstado, String observacion, Usuario usuarioQueActualiza)
            throws ReclamoNoEncontradoException, UsuarioNoValidoException {

        Reclamo reclamo = reclamos.get(idReclamo);
        if (reclamo == null) {
            throw new ReclamoNoEncontradoException("No se encontró el reclamo con ID: " + idReclamo);
        }

        if (usuarioQueActualiza instanceof Empleado && !reclamo.estaAsignadoA(usuarioQueActualiza)) {
            throw new UsuarioNoValidoException("Este reclamo no está asignado a usted.");
        }

        reclamo.actualizarEstado(nuevoEstado,
                observacion + " (Actualizado por: " + usuarioQueActualiza.getNombreUsuario() + ")");
    }

    public List<Reclamo> getTodosLosReclamos() {
        return new ArrayList<>(reclamos.values());
    }

    public List<Reclamo> getReclamosPorVecino(Vecino vecino) {
        return reclamos.values().stream()
                .filter(r -> r.fueHechoPor(vecino))
                .collect(Collectors.toList());
    }

    public List<Reclamo> getReclamosPorEmpleado(Empleado empleado) {
        return reclamos.values().stream()
                .filter(r -> r.estaAsignadoA(empleado))
                .collect(Collectors.toList());
    }

    public List<Reclamo> getTodosLosReclamosOrdenadosPorFecha() {
        return reclamos.values().stream()
                .sorted(Comparator.comparing(Reclamo::getFechaCreacion).reversed())
                .collect(Collectors.toList());
    }
}