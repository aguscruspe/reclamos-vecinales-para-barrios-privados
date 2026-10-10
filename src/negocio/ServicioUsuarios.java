package negocio;

import excepts.UsuarioNoValidoException;
import model.Usuario;
import model.Vecino;

import java.util.Map;

/**
 * SRP: Esta clase tiene la única responsabilidad de gestionar la lógica
 * de negocio relacionada con los Usuarios.
 */
public class ServicioUsuarios {

    private Map<String, Usuario> usuarios; // Referencia al mapa principal

    public ServicioUsuarios(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Autentica un usuario.
     * 
     * @return El Usuario si es exitoso, o null si falla.
     */
    public Usuario autenticar(String username, String password) {
        Usuario u = usuarios.get(username);
        if (u != null && u.autenticar(password)) {
            return u;
        }
        return null;
    }

    /**
     * Registra un nuevo vecino en el sistema.
     * Implementa la lógica de validación de duplicados.
     */
    public Vecino registrarVecino(String username, String password, String nombreCompleto, String dni, String direccion)
            throws UsuarioNoValidoException {

        // 1. Validar duplicado
        if (usuarios.containsKey(username)) {
            throw new UsuarioNoValidoException("El nombre de usuario '" + username + "' ya existe.");
        }

        // 2. Crear y validar (el modelo valida datos incompletos)
        Vecino nuevoVecino = new Vecino(username, password, nombreCompleto, dni, direccion);
        nuevoVecino.validar(); // Arroja UsuarioNoValidoException si faltan datos

        // 3. Añadir al mapa en memoria
        this.usuarios.put(nuevoVecino.getUsername(), nuevoVecino);

        return nuevoVecino;
    }

    public Map<String, Usuario> getTodosLosUsuarios() {
        return this.usuarios;
    }

    public Usuario getUsuarioPorUsername(String username) {
        return this.usuarios.get(username);
    }
}