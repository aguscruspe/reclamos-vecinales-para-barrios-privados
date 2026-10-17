package model;

public class Lote {

    private int numero;
    private String sector;

    public Lote(int numero, String sector) {
        this.numero = numero;
        this.sector = sector;
    }

    public int getNumero() {
        return numero;
    }

    public String getSector() {
        return sector;
    }

    @Override
    public String toString() {
        return "Lote " + numero + " - " + sector;
    }
}
