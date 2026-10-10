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

/**
 * Principio de Responsabilidad Única (SRP):
 * ... (resto de comentarios)
 */
public class GestorPersistencia implements IRepositorio {

    private static final String LOTES_FILE = "data/lotes.csv";
    private static final String USUARIOS_FILE = "data/usuarios.csv";
    private static final String RECLAMOS_FILE = "data/reclamos.csv";
    private static final String HISTORIAL_FILE = "data/historial.csv";
    private static final String DELIMITADOR = ";";

    public GestorPersistencia() {
        // Asegurar que el directorio de datos exista
        new File("data").mkdirs();
    }

    @Override
    public Map<Integer, Lote> cargarLotes() throws PersistenciaException {
        // Formato de cada línea: numero;sector
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
                String password = datos[3]; // <--- AHORA LEE LA CONTRASEÑA REAL
                String nombre = datos[4];
                String dni = datos[5];      // <--- AHORA LEE EL DNI REAL

                Usuario u = null;
                switch (tipoUsuario) {
                    case "Vecino":
                        // La columna 6 tiene el número de lote: lo traducimos al objeto Lote
                        Lote lote = lotes.get(Integer.parseInt(datos[6]));
                        u = new Vecino(id, nombreUsuario, password, nombre, dni, lote);
                        break;
                    case "Empleado":
                        u = new Empleado(id, nombreUsuario, password, nombre, dni,
                                AreaEmpleado.valueOf(datos[6]));
                        break;
                    case "Intendente":
                        u = new Intendente(id, nombreUsuario, password, nombre, dni);
                        break;
                }
                if (u != null) {
                    usuarios.put(u.getNombreUsuario(), u);
                }
            }
        } catch (FileNotFoundException e) {
            // Es normal si es la primera ejecución
            System.out.println("Archivo de usuarios no encontrado, se creará uno nuevo al guardar.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de usuarios.", e);
        }
        return usuarios;
    }

    @Override
    public Map<Integer, Reclamo> cargarReclamos(Map<String, Usuario> usuarios) throws PersistenciaException {
        // 1. Cargar todos los historiales
        Map<Integer, List<HistorialEstado>> historialesPorReclamo = cargarHistoriales();

        // 2. Cargar los reclamos y enlazarlos
        Map<Integer, Reclamo> reclamos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RECLAMOS_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(DELIMITADOR);
                int id = Integer.parseInt(datos[0]);
                TipoReclamo tipo = TipoReclamo.valueOf(datos[1]);
                String desc = datos[2];
                String ubicacion = datos[3];
                int idVecino = Integer.parseInt(datos[4]);
                int idEmpleado = Integer.parseInt(datos[5]); // 0 si es null

                // Buscamos los usuarios por ID (más eficiente que por nombreUsuario)
                Vecino vecino = (Vecino) usuarios.values().stream()
                        .filter(u -> u.getId() == idVecino && u instanceof Vecino)
                        .findFirst().orElse(null);

                Empleado empleado = (Empleado) usuarios.values().stream()
                        .filter(u -> u.getId() == idEmpleado && u instanceof Empleado)
                        .findFirst().orElse(null);

                if (vecino != null) {
                    List<HistorialEstado> historial = historialesPorReclamo.getOrDefault(id, new ArrayList<>());
                    Reclamo r = new Reclamo(id, tipo, desc, ubicacion, vecino, empleado, historial);
                    reclamos.put(r.getId(), r);
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
        Map<Integer, List<HistorialEstado>> mapa = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORIAL_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(DELIMITADOR);
                int idReclamo = Integer.parseInt(datos[0]);
                LocalDateTime fecha = LocalDateTime.parse(datos[1]);
                EstadoReclamo estado = EstadoReclamo.valueOf(datos[2]);
                String obs = datos[3];

                HistorialEstado h = new HistorialEstado(fecha, estado, obs);

                mapa.putIfAbsent(idReclamo, new ArrayList<>());
                mapa.get(idReclamo).add(h);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de historial no encontrado.");
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer el archivo de historial.", e);
        }
        return mapa;
    }

    @Override
    public void guardarUsuarios(List<Usuario> usuarios) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USUARIOS_FILE))) {
            for (Usuario u : usuarios) {

                // **** CAMBIOS APLICADOS (FIX BUGS LOGIN Y DNI) ****
                String lineaBase = u.getId() + DELIMITADOR +
                        u.getTipoUsuario() + DELIMITADOR +
                        u.getNombreUsuario() + DELIMITADOR +
                        u.getPassword() + DELIMITADOR + // <--- ARREGLADO
                        u.getNombreCompleto() + DELIMITADOR +
                        u.getDni(); // <--- ARREGLADO

                if (u instanceof Vecino) {
                    lineaBase += DELIMITADOR + ((Vecino) u).getLote().getNumero();
                } else if (u instanceof Empleado) {
                    lineaBase += DELIMITADOR + ((Empleado) u).getArea().name();
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
        // Sobrescribimos ambos archivos (reclamos e historial) para mantener
        // consistencia

        try (BufferedWriter bwReclamos = new BufferedWriter(new FileWriter(RECLAMOS_FILE));
             BufferedWriter bwHistorial = new BufferedWriter(new FileWriter(HISTORIAL_FILE))) {

            for (Reclamo r : reclamos) {
                int idEmpleado = (r.getEmpleadoAsignado() != null) ? r.getEmpleadoAsignado().getId() : 0;

                String lineaReclamo = r.getId() + DELIMITADOR +
                        r.getTipo().name() + DELIMITADOR +
                        r.getDescripcion() + DELIMITADOR +
                        r.getUbicacion() + DELIMITADOR +
                        r.getVecino().getId() + DELIMITADOR +
                        idEmpleado;
                bwReclamos.write(lineaReclamo);
                bwReclamos.newLine();

                // Guardar el historial de este reclamo
                for (HistorialEstado h : r.getHistorial()) {
                    String lineaHistorial = r.getId() + DELIMITADOR +
                            h.getFecha().toString() + DELIMITADOR +
                            h.getEstado().name() + DELIMITADOR +
                            h.getObservacion();
                    bwHistorial.write(lineaHistorial);
                    bwHistorial.newLine();
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar el archivo de reclamos o historial.", e);
        }
    }
}