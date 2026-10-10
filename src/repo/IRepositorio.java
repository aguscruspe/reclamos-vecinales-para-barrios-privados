package repo;

import excepts.PersistenciaException;
import model.Reclamo;
import model.Usuario;
import java.util.List;
import java.util.Map;

/**
 * Principio de Inversión de Dependencias (DIP):
 * Define la abstracción de la persistencia.
 * La capa de negocio (SistemaGestionReclamos) dependerá de esta
 * interfaz, no de la implementación concreta (GestorPersistencia).
 *
 * SRP: El contrato se centra únicamente en la persistencia.
 */
public interface IRepositorio {

    // Métodos para cargar el estado inicial del sistema
    Map<String, Usuario> cargarUsuarios() throws PersistenciaException;

    Map<Integer, Reclamo> cargarReclamos(Map<String, Usuario> usuarios) throws PersistenciaException;

    // Métodos para guardar el estado del sistema
    void guardarUsuarios(List<Usuario> usuarios) throws PersistenciaException;

    void guardarReclamos(List<Reclamo> reclamos) throws PersistenciaException;
}
