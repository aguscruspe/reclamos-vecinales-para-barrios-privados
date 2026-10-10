package model;

/**
 * Lote del barrio privado.
 * Es clase propia (y no un texto dentro de Vecino) porque varios vecinos
 * pueden vivir en el mismo lote y todos tienen que apuntar al mismo objeto.
 */
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
