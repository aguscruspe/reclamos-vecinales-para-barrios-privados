package repo;

import excepts.PersistenciaException;
import model.*;
import model.enums.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorPersistencia implements IRepositorio {

    private static final String LOTES_FILE = "data/lotes.csv";
    private static final String USUARIOS_FILE = "data/usuarios.csv";
    private static final String RECLAMOS_FILE = "data/reclamos.csv";
    private static final String HISTORIAL_FILE = "data/historial.csv";
    private static final String DELIMITADOR = ";";

    public GestorPersistencia() {
        new File("data").mkdirs();
    }

    @Override
    public Map<Integer, Lote> cargarLotes() throws PersistenciaException {

        Map<Integer, Lote> lotes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LOTES_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(DELIMITADOR);
                Lote lote = new Lote(Integer.parseInt(datos[0]), datos[1]);
                lotes.put(lote.getNumero(), lote);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de lotes no encontrado.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de lotes.", e);
        }
        return lotes;
    }

    @Override
    public Map<String, Usuario> cargarUsuarios(Map<Integer, Lote> lotes) throws PersistenciaException {
        Map<String, Usuario> usuarios = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USUARIOS_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(DELIMITADOR);
                int id = Integer.parseInt(datos[0]);
                String tipoUsuario = datos[1];
                String nombreUsuario = datos[2];
                String password = datos[3];
                String nombre = datos[4];
                String dni = datos[5];     

                Usuario usuario = null;
                switch (tipoUsuario) {
                    case "Vecino":
                        int numeroLote = Integer.parseInt(datos[6]);
                        Lote lote = lotes.get(numeroLote);
                        usuario = new Vecino(id, nombreUsuario, password, nombre, dni, lote);
                        break;
                    case "Empleado":
                        AreaEmpleado area = AreaEmpleado.valueOf(datos[6]);
                        usuario = new Empleado(id, nombreUsuario, password, nombre, dni, area);
                        break;
                    case "Intendente":
                        usuario = new Intendente(id, nombreUsuario, password, nombre, dni);
                        break;
                }
                if (usuario != null) {
                    usuarios.put(usuario.getNombreUsuario(), usuario);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de usuarios no encontrado, se creará uno nuevo al guardar.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de usuarios.", e);
        }
        return usuarios;
    }

    @Override
    public Map<Integer, Reclamo> cargarReclamos(Map<String, Usuario> usuarios) throws PersistenciaException {
        Map<Integer, List<HistorialEstado>> historialesPorReclamo = cargarHistoriales();

        Map<Integer, Reclamo> reclamos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RECLAMOS_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(DELIMITADOR);
                int id = Integer.parseInt(datos[0]);
                TipoReclamo tipo = TipoReclamo.valueOf(datos[1]);
                String descripcion = datos[2];
                String ubicacion = datos[3];
                int idVecino = Integer.parseInt(datos[4]);
                int idEmpleado = Integer.parseInt(datos[5]); 

                Vecino vecino = (Vecino) usuarios.values().stream()
                        .filter(u -> u.getId() == idVecino && u instanceof Vecino)
                        .findFirst().orElse(null);

                Empleado empleado = (Empleado) usuarios.values().stream()
                        .filter(u -> u.getId() == idEmpleado && u instanceof Empleado)
                        .findFirst().orElse(null);

                if (vecino != null) {
                    List<HistorialEstado> historial = historialesPorReclamo.getOrDefault(id, new ArrayList<>());
                    Reclamo reclamo = new Reclamo(id, tipo, descripcion, ubicacion, vecino, empleado, historial);
                    reclamos.put(reclamo.getId(), reclamo);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de reclamos no encontrado.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de reclamos.", e);
        }
        return reclamos;
    }

    private Map<Integer, List<HistorialEstado>> cargarHistoriales() throws PersistenciaException {
        Map<Integer, List<HistorialEstado>> historialesPorReclamo = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORIAL_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(DELIMITADOR);
                int idReclamo = Integer.parseInt(datos[0]);
                LocalDateTime fecha = LocalDateTime.parse(datos[1]);
                EstadoReclamo estado = EstadoReclamo.valueOf(datos[2]);
                String observacion = datos[3];

                HistorialEstado historialEstado = new HistorialEstado(fecha, estado, observacion);

                historialesPorReclamo.putIfAbsent(idReclamo, new ArrayList<>());
                historialesPorReclamo.get(idReclamo).add(historialEstado);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de historial no encontrado.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de historial.", e);
        }
        return historialesPorReclamo;
    }

    @Override
    public void guardarUsuarios(List<Usuario> usuarios) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USUARIOS_FILE))) {
            for (Usuario usuario : usuarios) {

                String lineaBase = usuario.getId() + DELIMITADOR +
                        usuario.getTipoUsuario() + DELIMITADOR +
                        usuario.getNombreUsuario() + DELIMITADOR +
                        usuario.getPassword() + DELIMITADOR +
                        usuario.getNombreCompleto() + DELIMITADOR +
                        usuario.getDni();

                if (usuario instanceof Vecino) {
                    lineaBase += DELIMITADOR + ((Vecino) usuario).getLote().getNumero();
                } else if (usuario instanceof Empleado) {
                    lineaBase += DELIMITADOR + ((Empleado) usuario).getArea().name();
                }

                bw.write(lineaBase);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el archivo de usuarios.", e);
        }
    }

    @Override
    public void guardarReclamos(List<Reclamo> reclamos) throws PersistenciaException {

        try (BufferedWriter bwReclamos = new BufferedWriter(new FileWriter(RECLAMOS_FILE));
             BufferedWriter bwHistorial = new BufferedWriter(new FileWriter(HISTORIAL_FILE))) {

            for (Reclamo reclamo : reclamos) {
                int idEmpleado = (reclamo.getEmpleadoAsignado() != null) ? reclamo.getEmpleadoAsignado().getId() : 0;

                String lineaReclamo = reclamo.getId() + DELIMITADOR +
                        reclamo.getTipo().name() + DELIMITADOR +
                        reclamo.getDescripcion() + DELIMITADOR +
                        reclamo.getUbicacion() + DELIMITADOR +
                        reclamo.getVecino().getId() + DELIMITADOR +
                        idEmpleado;
                bwReclamos.write(lineaReclamo);
                bwReclamos.newLine();

                for (HistorialEstado historialEstado : reclamo.getHistorial()) {
                    String lineaHistorial = reclamo.getId() + DELIMITADOR +
                            historialEstado.getFecha().toString() + DELIMITADOR +
                            historialEstado.getEstado().name() + DELIMITADOR +
                            historialEstado.getObservacion();
                    bwHistorial.write(lineaHistorial);
                    bwHistorial.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el archivo de reclamos o historial.", e);
        }
    }
}