package model;

import excepts.UsuarioNoValidoException;

public class Vecino extends Usuario {

    private Lote lote;

    public Vecino(String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote) {
        super(nombreUsuario, password, nombreCompleto, dni);
        this.lote = lote;
    }

    // Constructor para carga desde persistencia
    public Vecino(int id, String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote) {
        super(id, nombreUsuario, password, nombreCompleto, dni);
        this.lote = lote;
    }

    public Lote getLote() {
        return lote;
    }

    @Override
    public String getTipoUsuario() {
        return "Vecino";
    }

    /**
     * Implementación de la interfaz IValidable.
     * Un vecino tiene que vivir en un lote del barrio.
     */
    @Override
    public void validar() throws UsuarioNoValidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                lote == null) {
            throw new UsuarioNoValidoException("Datos incompletos para el vecino.");
        }
    }
}
