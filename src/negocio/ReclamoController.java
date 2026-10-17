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

public class ReclamoController {


    private IRepositorio repositorio;

    private UsuarioService usuarioService;
    private ReclamoService reclamoService;

    private Map<Integer, Lote> lotes; 
    private Map<String, Usuario> usuarios; 
    private Map<Integer, Reclamo> reclamos; 

    private Usuario usuarioLogueado;

    public ReclamoController(IRepositorio repositorio) {
        this.repositorio = repositorio;
        this.usuarioLogueado = null;
    }

    public void iniciarSistema() throws PersistenciaException {

        this.lotes = repositorio.cargarLotes();
        this.usuarios = repositorio.cargarUsuarios(this.lotes);
        this.reclamos = repositorio.cargarReclamos(this.usuarios);

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


    public boolean login(String nombreUsuario, String password) {
        Usuario usuario = usuarioService.autenticar(nombreUsuario, password);
        if (usuario != null) {
            this.usuarioLogueado = usuario;
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

    public Vecino registrarVecino(String nombreUsuario, String password, String nombreCompleto, String dni, Lote lote)
            throws UsuarioNoValidoException, PersistenciaException {

        Vecino nuevoVecino = usuarioService.registrarVecino(nombreUsuario, password, nombreCompleto, dni, lote);

        guardarCambios();

        System.out.println("Nuevo vecino registrado con ID: " + nuevoVecino.getId());
        return nuevoVecino;
    }

    public List<Lote> getLotes() {
        return lotes.values().stream()
                .sorted(Comparator.comparingInt(Lote::getNumero))
                .collect(Collectors.toList());
    }


    public Reclamo crearReclamo(TipoReclamo tipo, String descripcion, String ubicacion)
            throws UsuarioNoValidoException, PersistenciaException {
        if (!(usuarioLogueado instanceof Vecino)) {
            throw new UsuarioNoValidoException("Solo los vecinos pueden crear reclamos.");
        }
        Vecino vecino = (Vecino) usuarioLogueado;

        Reclamo nuevoReclamo = reclamoService.crearReclamo(tipo, descripcion, ubicacion, vecino);

    
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

        reclamoService.asignarReclamo(idReclamo, usernameEmpleado, usuarioLogueado.getNombreUsuario());

        guardarCambios();

        System.out.printf("Reclamo %d asignado a %s.\n", idReclamo, usernameEmpleado);
    }

    public void actualizarEstadoReclamo(int idReclamo, EstadoReclamo nuevoEstado, String observacion)
            throws ReclamoNoEncontradoException, PersistenciaException, UsuarioNoValidoException {
        if (usuarioLogueado == null || usuarioLogueado instanceof Vecino) {
            throw new UsuarioNoValidoException("Acción no permitida para este usuario.");
        }

        reclamoService.actualizarEstadoReclamo(idReclamo, nuevoEstado, observacion, usuarioLogueado);

        guardarCambios();

        System.out.printf("Reclamo %d actualizado a estado %s.\n", idReclamo, nuevoEstado);
    }

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

    public void crearDatosDePruebaSiNoExisten() throws PersistenciaException, UsuarioNoValidoException {
        if (this.usuarios.isEmpty()) {
            System.out.println("Creando datos de prueba...");
            
            if (this.lotes.isEmpty()) {
                System.out.println("No hay lotes cargados (data/lotes.csv): no se crean datos de prueba.");
                return;
            }
            registrarVecino("vecino", "123", "Lautaro Vita", "30123456", getLotes().get(0));

            Empleado empleadoMantenimiento = new Empleado("empleado", "123", "Franco Brossi", "28123456",
                    AreaEmpleado.MANTENIMIENTO);
            Intendente intendente = new Intendente("intendente", "123", "Intendente General", "25123456");
            empleadoMantenimiento.validar();
            intendente.validar();
            usuarios.put(empleadoMantenimiento.getNombreUsuario(), empleadoMantenimiento);
            usuarios.put(intendente.getNombreUsuario(), intendente);

            login("vecino", "123");
            
            crearReclamo(TipoReclamo.ELECTRICIDAD, "No funciona la luminaria de la esquina", "Calle 5 y Calle 8");
            logout();

            guardarCambios();
            System.out.println("Datos de prueba creados y guardados.");
        }
    }
}