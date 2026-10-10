// Archivo: Intendente.java
package model;

import excepts.UsuarioNoValidoException;

public class Intendente extends Usuario {

    public Intendente(String nombreUsuario, String password, String nombreCompleto, String dni) {
        super(nombreUsuario, password, nombreCompleto, dni);
    }

    // Constructor para carga desde persistencia
    public Intendente(int id, String nombreUsuario, String password, String nombreCompleto, String dni) {
        super(id, nombreUsuario, password, nombreCompleto, dni);
    }

    @Override
    public String getTipoUsuario() {
        return "Intendente";
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty()) {
            throw new UsuarioNoValidoException("Datos incompletos para el intendente.");
        }
    }
}