package repo;

import excepts.PersistenciaException;
import model.Lote;
import model.Reclamo;
import model.Usuario;
import java.util.List;
import java.util.Map;

public interface IRepositorio {

    Map<Integer, Lote> cargarLotes() throws PersistenciaException;

    Map<String, Usuario> cargarUsuarios(Map<Integer, Lote> lotes) throws PersistenciaException;

    Map<Integer, Reclamo> cargarReclamos(Map<String, Usuario> usuarios) throws PersistenciaException;

    void guardarUsuarios(List<Usuario> usuarios) throws PersistenciaException;

    void guardarReclamos(List<Reclamo> reclamos) throws PersistenciaException;
}
