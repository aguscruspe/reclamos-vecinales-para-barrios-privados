package model;

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
    public String getTipoUsuario() {
        return "Vecino";
    }
}
