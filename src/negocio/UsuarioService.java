package negocio;

import excepts.UsuarioNoValidoException;
import model.Lote;
import model.Usuario;
import model.Vecino;

import java.util.Map;

public class UsuarioService {

    private Map<String, Usuario> usuarios; 

    public UsuarioService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario autenticar(String nombreUsuario, String password) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario != null && usuario.autenticar(password)) {
            return usuario;
        }
        return null;
    }

    public Vecino registrarVecino(String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote)
            throws UsuarioNoValidoException {

        if (usuarios.containsKey(nombreUsuario)) {
            throw new UsuarioNoValidoException("El nombre de usuario '" + nombreUsuario + "' ya existe.");
        }

        Vecino nuevoVecino = new Vecino(nombreUsuario, password, nombreCompleto, dni, lote);
        nuevoVecino.validar(); // Arroja UsuarioNoValidoException si faltan datos

        this.usuarios.put(nuevoVecino.getNombreUsuario(), nuevoVecino);

        return nuevoVecino;
    }

    public Map<String, Usuario> getTodosLosUsuarios() {
        return this.usuarios;
    }
}