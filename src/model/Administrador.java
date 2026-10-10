// Archivo: Administrador.java
package model;

import excepts.UsuarioNoValidoException;

public class Administrador extends Usuario {

    public Administrador(String username, String password, String nombreCompleto, String dni) {
        super(username, password, nombreCompleto, dni);
    }

    // Constructor para carga desde persistencia
    public Administrador(int id, String username, String password, String nombreCompleto, String dni) {
        super(id, username, password, nombreCompleto, dni);
    }

    @Override
    public String getTipoUsuario() {
        return "Administrador";
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty()) {
            throw new UsuarioNoValidoException("Datos incompletos para el administrador.");
        }
    }
}