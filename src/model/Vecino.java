package model;

import excepts.UsuarioNoValidoException;

public class Vecino extends Usuario {

    private Lote lote;

    public Vecino(String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote) {
        super(nombreUsuario, password, nombreCompleto, dni);
        this.lote = lote;
    }

    public Vecino(int id, String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote) {
        super(id, nombreUsuario, password, nombreCompleto, dni);
        this.lote = lote;
    }

    public Lote getLote() {
        return lote;
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        super.validar();
        if (lote == null) {
            throw new UsuarioNoValidoException("El vecino debe tener un lote.");
        }
    }

    @Override
    public String getTipoUsuario() {
        return "Vecino";
    }
}
