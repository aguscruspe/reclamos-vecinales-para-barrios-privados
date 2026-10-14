// Archivo: Intendente.java
package model;


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
}