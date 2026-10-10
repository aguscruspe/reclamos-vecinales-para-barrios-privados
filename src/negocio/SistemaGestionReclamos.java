package negocio;

import excepts.*;
import model.*;
import model.enums.*;
import repo.IRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GRASP - Controller (o Facade):
 * Esta clase actúa como la fachada principal del sistema.
 * Es el único punto de entrada para la capa de Vista.
 * Delega la lógica de negocio a servicios especializados (SRP).
 */
public class SistemaGestionReclamos {

    // Capa de Persistencia
    private IRepositorio repositorio;

    // Servicios de Lógica de Negocio
    private ServicioUsuarios servicioUsuarios;
    private ServicioReclamos servicioReclamos;

    // Estado en memoria
    private Map<String, Usuario> usuarios; // Acceso rápido por username
    private Map<Integer, Reclamo> reclamos; // Acceso rápido por ID

    // Estado de Sesión
    private Usuario usuarioLogueado;

    public SistemaGestionReclamos(IRepositorio repositorio) {
        this.repositorio = repositorio;
        this.usuarioLogueado = null;
    }

    public void iniciarSistema() throws PersistenciaException {
        // Cargar datos del repositorio
        this.usuarios = repositorio.cargarUsuarios();
        this.reclamos = repositorio.cargarReclamos(this.usuarios);

        // Inyectar los mapas en los servicios
        this.servicioUsuarios = new ServicioUsuarios(this.usuarios);
        this.servicioReclamos = new ServicioReclamos(this.reclamos, this.usuarios);

        System.out.println(
                "Sistema iniciado. " + usuarios.size() + " usuarios y " + reclamos.size() + " reclamos cargados.");
    }

    private void guardarCambios() throws PersistenciaException {
        // Persiste el estado actual
        repositorio.guardarUsuarios(new ArrayList<>(usuarios.values()));
        repositorio.guardarReclamos(new ArrayList<>(reclamos.values()));
        System.out.println("Cambios guardados en los archivos.");
    }

    // --- Gestión de Sesión ---

    public boolean login(String username, String password) {
        // Delega la autenticación al servicio
        Usuario u = servicioUsuarios.autenticar(username, password);
        if (u != null) {
            this.usuarioLogueado = u;
            return true;
        }
        return false;
    }

    public void logout() {
        this.usuarioLogueado = null;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // --- (NUEVO) Lógica de Registro (Corrección #2) ---
    public Vecino registrarVecino(String username, String password, String nombreCompleto, String dni, String direccion)
            throws UsuarioNoValidoException, PersistenciaException {

        // 1. Delega la creación y validación al servicio
        Vecino nuevoVecino = servicioUsuarios.registrarVecino(username, password, nombreCompleto, dni, direccion);

        // 2. La fachada se encarga de persistir
        guardarCambios();

        System.out.println("Nuevo vecino registrado con ID: " + nuevoVecino.getId());
        return nuevoVecino;
    }

    // --- Lógica de Negocio (Reclamos) ---

    public Reclamo crearReclamo(TipoReclamo tipo, String descripcion, String ubicacion)
            throws UsuarioNoValidoException, PersistenciaException {
        if (!(usuarioLogueado instanceof Vecino)) {
            throw new UsuarioNoValidoException("Solo los vecinos pueden crear reclamos.");
        }
        Vecino vecino = (Vecino) usuarioLogueado;

        // 1. Delega la creación al servicio
        Reclamo nuevoReclamo = servicioReclamos.crearReclamo(tipo, descripcion, ubicacion, vecino);

        // 2. La fachada persiste
        guardarCambios();

        System.out.println("Nuevo reclamo creado con ID: " + nuevoReclamo.getId());
        return nuevoReclamo;
    }

    public void asignarReclamo(int idReclamo, String usernameEmpleado)
            throws UsuarioNoValidoException, ReclamoNoEncontradoException, PersistenciaException {
        if (!(usuarioLogueado instanceof Administrador)) {
            throw new UsuarioNoValidoException("Solo los administradores pueden asignar reclamos.");
        }

        // 1. Delega la asignación al servicio
        servicioReclamos.asignarReclamo(idReclamo, usernameEmpleado, usuarioLogueado.getUsername());

        // 2. La fachada persiste
        guardarCambios();

        System.out.printf("Reclamo %d asignado a %s.\n", idReclamo, usernameEmpleado);
    }

    public void actualizarEstadoReclamo(int idReclamo, EstadoReclamo nuevoEstado, String observacion)
            throws ReclamoNoEncontradoException, PersistenciaException, UsuarioNoValidoException {
        if (usuarioLogueado == null || usuarioLogueado instanceof Vecino) {
            throw new UsuarioNoValidoException("Acción no permitida para este usuario.");
        }

        // 1. Delega la actualización al servicio
        servicioReclamos.actualizarEstadoReclamo(idReclamo, nuevoEstado, observacion, usuarioLogueado);

        // 2. La fachada persiste
        guardarCambios();

        System.out.printf("Reclamo %d actualizado a estado %s.\n", idReclamo, nuevoEstado);
    }

    // --- Métodos de Consulta (Simples delegaciones) ---

    public Map<String, Usuario> getTodosLosUsuarios() {
        return servicioUsuarios.getTodosLosUsuarios();
    }

    public List<Reclamo> getTodosLosReclamos() {
        return servicioReclamos.getTodosLosReclamos();
    }

    public List<Reclamo> getReclamosPorVecino(Vecino vecino) {
        return servicioReclamos.getReclamosPorVecino(vecino);
    }

    public List<Reclamo> getReclamosPorEmpleado(EmpleadoMunicipal empleado) {
        return servicioReclamos.getReclamosPorEmpleado(empleado);
    }

    public List<Reclamo> getTodosLosReclamosOrdenadosPorFecha() {
        return servicioReclamos.getTodosLosReclamosOrdenadosPorFecha();
    }

    public Set<SectorEmpleado> getSectoresConReclamosAsignados() {
        return servicioReclamos.getSectoresConReclamosAsignados();
    }

    // --- Datos de Prueba (Se mantiene en la fachada) ---

    public void crearDatosDePruebaSiNoExisten() throws PersistenciaException, UsuarioNoValidoException {
        if (this.usuarios.isEmpty()) {
            System.out.println("Creando datos de prueba...");
            // Crear usuarios (usando el método de registro)
            registrarVecino("vecino", "123", "Lautaro Vita", "30123456", "Calle Falsa 123");

            // Creamos los otros usuarios directamente en el mapa (ya que no hay registro
            // para ellos)
            EmpleadoMunicipal e1 = new EmpleadoMunicipal("empleado", "123", "Franco Brossi", "28123456",
                    SectorEmpleado.VIALIDAD);
            Administrador a1 = new Administrador("admin", "123", "Admin General", "25123456");
            e1.validar();
            a1.validar();
            usuarios.put(e1.getUsername(), e1);
            usuarios.put(a1.getUsername(), a1);

            // Crear reclamo
            login("vecino", "123");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            } // Pausa
            crearReclamo(TipoReclamo.BACHE, "Bache peligroso", "Av. Siempre Viva 742");
            logout();

            // Guardamos los usuarios (admin/empleado) que no se guardaron con
            // registrarVecino
            guardarCambios();
            System.out.println("Datos de prueba creados y guardados.");
        }
    }
}