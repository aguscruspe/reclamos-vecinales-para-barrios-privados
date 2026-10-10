package negocio;

import excepts.UsuarioNoValidoException;
import model.Usuario;
import model.Vecino;

import java.util.Map;

/**
 * SRP: Esta clase tiene la única responsabilidad de gestionar la lógica
 * de negocio relacionada con los Usuarios.
 */
public class UsuarioService {

    private Map<String, Usuario> usuarios; // Referencia al mapa principal

    public UsuarioService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Autentica un usuario.
     * 
     * @return El Usuario si es exitoso, o null si falla.
     */
    public Usuario autenticar(String nombreUsuario, String password) {
        Usuario u = usuarios.get(nombreUsuario);
        if (u != null && u.autenticar(password)) {
            return u;
        }
        return null;
    }

    /**
     * Registra un nuevo vecino en el sistema.
     * Implementa la lógica de validación de duplicados.
     */
    public Vecino registrarVecino(String nombreUsuario, String password, String nombreCompleto, String dni, String direccion)
            throws UsuarioNoValidoException {

        // 1. Validar duplicado
        if (usuarios.containsKey(nombreUsuario)) {
            throw new UsuarioNoValidoException("El nombre de usuario '" + nombreUsuario + "' ya existe.");
        }

        // 2. Crear y validar (el modelo valida datos incompletos)
        Vecino nuevoVecino = new Vecino(nombreUsuario, password, nombreCompleto, dni, direccion);
        nuevoVecino.validar(); // Arroja UsuarioNoValidoException si faltan datos

        // 3. Añadir al mapa en memoria
        this.usuarios.put(nuevoVecino.getNombreUsuario(), nuevoVecino);

        return nuevoVecino;
    }

    public Map<String, Usuario> getTodosLosUsuarios() {
        return this.usuarios;
    }

    public Usuario getUsuarioPorUsername(String nombreUsuario) {
        return this.usuarios.get(nombreUsuario);
    }
}