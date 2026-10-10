package repo;

import excepts.PersistenciaException;
import model.Lote;
import model.Reclamo;
import model.Usuario;
import java.util.List;
import java.util.Map;

/**
 * Principio de Inversión de Dependencias (DIP):
 * Define la abstracción de la persistencia.
 * La capa de negocio (ReclamoController) dependerá de esta
 * interfaz, no de la implementación concreta (GestorPersistencia).
 *
 * SRP: El contrato se centra únicamente en la persistencia.
 */
public interface IRepositorio {

    // Métodos para cargar el estado inicial del sistema
    Map<Integer, Lote> cargarLotes() throws PersistenciaException;

    // Recibe los lotes para poder enlazar cada Vecino con su objeto Lote
    Map<String, Usuario> cargarUsuarios(Map<Integer, Lote> lotes) throws PersistenciaException;

    Map<Integer, Reclamo> cargarReclamos(Map<String, Usuario> usuarios) throws PersistenciaException;

    // Métodos para guardar el estado del sistema
    void guardarUsuarios(List<Usuario> usuarios) throws PersistenciaException;

    void guardarReclamos(List<Reclamo> reclamos) throws PersistenciaException;
}
