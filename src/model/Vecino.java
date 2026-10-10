package model;

import excepts.UsuarioNoValidoException;

public class Vecino extends Usuario {

    private String direccion;

    public Vecino(String nombreUsuario, String password, String nombreCompleto, String dni, String direccion) {
        super(nombreUsuario, password, nombreCompleto, dni);
        this.direccion = direccion;
    }

    // Constructor para carga desde persistencia
    public Vecino(int id, String nombreUsuario, String password, String nombreCompleto, String dni, String direccion) {
        super(id, nombreUsuario, password, nombreCompleto, dni);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String getTipoUsuario() {
        return "Vecino";
    }

    /**
     * Implementación de la interfaz IValidable.
     * Define la lógica de validación específica para un Vecino.
     */
    @Override
    public void validar() throws UsuarioNoValidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                direccion == null || direccion.trim().isEmpty()) {
            throw new UsuarioNoValidoException("Datos incompletos para el vecino.");
        }
    }
}