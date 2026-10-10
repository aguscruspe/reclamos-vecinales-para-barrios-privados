package negocio;

import excepts.*;
import model.*;
import model.enums.*;
import repo.IRepositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GRASP - Controlador:
 * Es el único punto de entrada para la capa de Vista.
 * Delega la lógica de negocio a servicios especializados (SRP).
 */
public class ReclamoController {

    // Capa de Persistencia
    private IRepositorio repositorio;

    // Servicios de Lógica de Negocio
    private UsuarioService usuarioService;
    private ReclamoService reclamoService;

    // Estado en memoria
    private Map<Integer, Lote> lotes; // Acceso rápido por número de lote
    private Map<String, Usuario> usuarios; // Acceso rápido por nombreUsuario
    private Map<Integer, Reclamo> reclamos; // Acceso rápido por ID

    // Estado de Sesión
    private Usuario usuarioLogueado;

    public ReclamoController(IRepositorio repositorio) {
        this.repositorio = repositorio;
        this.usuarioLogueado = null;
    }

    public void iniciarSistema() throws PersistenciaException {
        // Cargar datos del repositorio
        this.lotes = repositorio.cargarLotes();
        this.usuarios = repositorio.cargarUsuarios(this.lotes);
        this.reclamos = repositorio.cargarReclamos(this.usuarios);

        // Inyectar los mapas en los servicios
        this.usuarioService = new UsuarioService(this.usuarios);
        this.reclamoService = new ReclamoService(this.reclamos, this.usuarios);

        System.out.println(
                "Sistema iniciado. " + lotes.size() + " lotes, " + usuarios.size() + " usuarios y " + reclamos.size() + " reclamos cargados.");
    }

    private void guardarCambios() throws PersistenciaException {
        // Persiste el estado actual
        repositorio.guardarUsuarios(new ArrayList<>(usuarios.values()));
        repositorio.guardarReclamos(new ArrayList<>(reclamos.values()));
        System.out.println("Cambios guardados en los archivos.");
    }

    // --- Gestión de Sesión ---

    public boolean login(String nombreUsuario, String password) {
        // Delega la autenticación al servicio
        Usuario u = usuarioService.autenticar(nombreUsuario, password);
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
    public Vecino registrarVecino(String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote)
            throws UsuarioNoValidoException, PersistenciaException {

        // 1. Delega la creación y validación al servicio
        Vecino nuevoVecino = usuarioService.registrarVecino(nombreUsuario, password, nombreCompleto, dni, lote);

        // 2. El controlador se encarga de persistir
        guardarCambios();

        System.out.println("Nuevo vecino registrado con ID: " + nuevoVecino.getId());
        return nuevoVecino;
    }

    /**
     * Lotes del barrio, ordenados por número.
     * Los lotes vienen precargados en data/lotes.csv (no hay alta de lotes desde el sistema).
     */
    public List<Lote> getLotes() {
        return lotes.values().stream()
                .sorted(Comparator.comparingInt(Lote::getNumero))
                .collect(Collectors.toList());
    }

    // --- Lógica de Negocio (Reclamos) ---

    public Reclamo crearReclamo(TipoReclamo tipo, String descripcion, String ubicacion)
            throws UsuarioNoValidoException, PersistenciaException {
        if (!(usuarioLogueado instanceof Vecino)) {
            throw new UsuarioNoValidoException("Solo los vecinos pueden crear reclamos.");
        }
        Vecino vecino = (Vecino) usuarioLogueado;

        // 1. Delega la creación al servicio
        Reclamo nuevoReclamo = reclamoService.crearReclamo(tipo, descripcion, ubicacion, vecino);

        // 2. El controlador persiste
        guardarCambios();

        System.out.println("Nuevo reclamo creado con ID: " + nuevoReclamo.getId());
        return nuevoReclamo;
    }

    public void asignarReclamo(int idReclamo, String usernameEmpleado)
            throws UsuarioNoValidoException, ReclamoNoEncontradoException, PersistenciaException,
            AsignacionInvalidaException {
        if (!(usuarioLogueado instanceof Intendente)) {
            throw new UsuarioNoValidoException("Solo el intendente puede asignar reclamos.");
        }

        // 1. Delega la asignación al servicio
        reclamoService.asignarReclamo(idReclamo, usernameEmpleado, usuarioLogueado.getNombreUsuario());

        // 2. El controlador persiste
        guardarCambios();

        System.out.printf("Reclamo %d asignado a %s.\n", idReclamo, usernameEmpleado);
    }

    public void actualizarEstadoReclamo(int idReclamo, EstadoReclamo nuevoEstado, String observacion)
            throws ReclamoNoEncontradoException, PersistenciaException, UsuarioNoValidoException {
        if (usuarioLogueado == null || usuarioLogueado instanceof Vecino) {
            throw new UsuarioNoValidoException("Acción no permitida para este usuario.");
        }

        // 1. Delega la actualización al servicio
        reclamoService.actualizarEstadoReclamo(idReclamo, nuevoEstado, observacion, usuarioLogueado);

        // 2. El controlador persiste
        guardarCambios();

        System.out.printf("Reclamo %d actualizado a estado %s.\n", idReclamo, nuevoEstado);
    }

    // --- Métodos de Consulta (Simples delegaciones) ---

    public Map<String, Usuario> getTodosLosUsuarios() {
        return usuarioService.getTodosLosUsuarios();
    }

    public List<Reclamo> getTodosLosReclamos() {
        return reclamoService.getTodosLosReclamos();
    }

    public List<Reclamo> getReclamosPorVecino(Vecino vecino) {
        return reclamoService.getReclamosPorVecino(vecino);
    }

    public List<Reclamo> getReclamosPorEmpleado(Empleado empleado) {
        return reclamoService.getReclamosPorEmpleado(empleado);
    }

    public List<Reclamo> getTodosLosReclamosOrdenadosPorFecha() {
        return reclamoService.getTodosLosReclamosOrdenadosPorFecha();
    }

    public Set<AreaEmpleado> getAreasConReclamosAsignados() {
        return reclamoService.getAreasConReclamosAsignados();
    }

    // --- Datos de Prueba (Se mantiene en el controlador) ---

    public void crearDatosDePruebaSiNoExisten() throws PersistenciaException, UsuarioNoValidoException {
        if (this.usuarios.isEmpty()) {
            System.out.println("Creando datos de prueba...");
            // Crear usuarios (usando el método de registro)
            if (this.lotes.isEmpty()) {
                System.out.println("No hay lotes cargados (data/lotes.csv): no se crean datos de prueba.");
                return;
            }
            registrarVecino("vecino", "123", "Lautaro Vita", "30123456", getLotes().get(0));

            // Creamos los otros usuarios directamente en el mapa (ya que no hay registro
            // para ellos)
            Empleado e1 = new Empleado("empleado", "123", "Franco Brossi", "28123456",
                    AreaEmpleado.MANTENIMIENTO);
            Intendente a1 = new Intendente("intendente", "123", "Intendente General", "25123456");
            e1.validar();
            a1.validar();
            usuarios.put(e1.getNombreUsuario(), e1);
            usuarios.put(a1.getNombreUsuario(), a1);

            // Crear reclamo
            login("vecino", "123");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            } // Pausa
            crearReclamo(TipoReclamo.ELECTRICIDAD, "No funciona la luminaria de la esquina", "Calle 5 y Calle 8");
            logout();

            // Guardamos los usuarios (intendente/empleado) que no se guardaron con
            // registrarVecino
            guardarCambios();
            System.out.println("Datos de prueba creados y guardados.");
        }
    }
}