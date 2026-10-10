package model;

import model.interfaces.IValidable;

/**
 * Clase abstracta base para todos los tipos de usuarios.
 * Aplica Principio de Sustitución de Liskov (LSP):
 * Cualquier Vecino, Empleado o Admin puede ser tratado como un Usuario.
 */
public abstract class Usuario implements IValidable {

    private static int contadorId = 0;
    protected int id;
    protected String nombreUsuario;
    protected String password;
    protected String nombreCompleto;
    protected String dni;

    public Usuario(String nombreUsuario, String password, String nombreCompleto, String dni) {
        this.id = ++contadorId;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
    }

    // Constructor para carga desde persistencia
    public Usuario(int id, String nombreUsuario, String password, String nombreCompleto, String dni) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        if (id > contadorId)
            contadorId = id; // Actualizar contador estático
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

    // **** AÑADIDO (FIX BUG LOGIN) ****
    public String getPassword() {
        return this.password;
    }

    // **** AÑADIDO (FIX BUG DNI) ****
    public String getDni() {
        return dni;
    }

    public boolean autenticar(String password) {
        return this.password.equals(password);
    }

    // Método abstracto (polimorfismo) que las subclases deben implementar.
    public abstract String getTipoUsuario();

    @Override
    public String toString() {
        return String.format("ID: %d, Username: %s, Nombre: %s, Tipo: %s",
                id, nombreUsuario, nombreCompleto, getTipoUsuario());
    }
}