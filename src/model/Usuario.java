package model;

import model.interfaces.IValidable;
import excepts.UsuarioNoValidoException;

public abstract class Usuario implements IValidable {

    private static int contadorId = 0;
    private int id;
    private String nombreUsuario;
    private String password;
    private String nombreCompleto;
    private String dni;

    public Usuario(String nombreUsuario, String password, String nombreCompleto, String dni) {
        this.id = ++contadorId;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
    }

    public Usuario(int id, String nombreUsuario, String password, String nombreCompleto, String dni) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        if (id > contadorId)
            contadorId = id;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDni() {
        return dni;
    }

    public boolean autenticar(String password) {
        return this.password.equals(password);
    }

    public abstract String getTipoUsuario();

    @Override
    public String toString() {
        return String.format("ID: %d, Username: %s, Nombre: %s, Tipo: %s",
                id, nombreUsuario, nombreCompleto, getTipoUsuario());
    }

    @Override
    public void validar() throws UsuarioNoValidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                dni == null || dni.trim().isEmpty() ||
                nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new UsuarioNoValidoException("Faltan datos obligatorios del usuario.");
        }
    }
}