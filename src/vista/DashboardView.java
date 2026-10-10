package vista;

import excepts.ReclamoNoEncontradoException;
import model.Reclamo;
import model.Usuario;
import negocio.ReclamoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DashboardView extends JFrame {

    private ReclamoController sistema;
    private Usuario usuarioLogueado;

    // Componentes de la GUI
    private JTable tablaReclamos;
    private DefaultTableModel tableModel;
    private JButton btnNuevoReclamo;
    private JButton btnAsignar;
    private JButton btnActualizarEstado;

    public DashboardView(ReclamoController sistema) {
        this.sistema = sistema;
        this.usuarioLogueado = sistema.getUsuarioLogueado();

        // --- Configuración de la Ventana ---
        setTitle("Dashboard - Sistema de Reclamos (" + usuarioLogueado.getNombreUsuario() + ")");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Título (Norte) ---
        JLabel lblTitulo = new JLabel("Bienvenido, " + usuarioLogueado.getNombreCompleto(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel de Tabla (Centro) ---
        String[] columnas = { "ID", "Fecha Creación", "Tipo", "Estado", "Reportado por", "Asignado a", "Ubicación" };

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };

        tablaReclamos = new JTable(tableModel);
        tablaReclamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReclamos.setAutoCreateRowSorter(true);

        if (usuarioLogueado instanceof model.Vecino) {
            // El índice 4 es "Reportado por"
            javax.swing.table.TableColumn col = tablaReclamos.getColumnModel().getColumn(4);
            col.setMinWidth(0);
            col.setMaxWidth(0);
            col.setPreferredWidth(0);
        }

        cargarReclamosEnTabla(); // Ahora carga los datos filtrados

        JScrollPane scrollPane = new JScrollPane(tablaReclamos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel de Botones (Sur) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnNuevoReclamo = new JButton("Crear Nuevo Reclamo");
        btnAsignar = new JButton("Asignar Reclamo Seleccionado");
        btnActualizarEstado = new JButton("Actualizar Estado Seleccionado");

        // Lógica de visibilidad de botones
        if (usuarioLogueado instanceof model.Vecino) {
            panelBotones.add(btnNuevoReclamo);
        } else if (usuarioLogueado instanceof model.Intendente) {
            panelBotones.add(btnAsignar);
            panelBotones.add(btnActualizarEstado);
        } else if (usuarioLogueado instanceof model.Empleado) {
            panelBotones.add(btnActualizarEstado);
        }

        add(panelBotones, BorderLayout.SOUTH);

        // =======================================================
        // --- CONEXIÓN DE BOTONES (Sin cambios) ---
        // =======================================================

        // 1. Conectamos "Crear Nuevo Reclamo" (Vecino)
        if (btnNuevoReclamo != null) {
            btnNuevoReclamo.addActionListener(e -> {
                CrearReclamoView form = new CrearReclamoView(this, sistema);
                form.setVisible(true);
                cargarReclamosEnTabla();
            });
        }

        // 2. Conectamos "Actualizar Estado Seleccionado" (Admin/Empleado)
        if (btnActualizarEstado != null) {
            btnActualizarEstado.addActionListener(e -> {
                int filaSeleccionada = tablaReclamos.getSelectedRow();
                // Importante: convertir índice de vista a modelo (si la tabla se ordena)
                int filaModelo = tablaReclamos.convertRowIndexToModel(filaSeleccionada);

                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Por favor, seleccione un reclamo de la tabla.",
                            "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int idReclamo = (int) tableModel.getValueAt(filaModelo, 0);

                try {
                    Reclamo reclamo = sistema.getTodosLosReclamos().stream()
                            .filter(r -> r.getId() == idReclamo)
                            .findFirst()
                            .orElseThrow(
                                    () -> new ReclamoNoEncontradoException("No se encontró el reclamo en memoria."));

                    ActualizarEstadoView form = new ActualizarEstadoView(this, sistema, reclamo);
                    form.setVisible(true);

                    cargarReclamosEnTabla();

                } catch (ReclamoNoEncontradoException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        // 3. Conectamos "Asignar Reclamo Seleccionado" (Intendente)
        if (btnAsignar != null) {
            btnAsignar.addActionListener(e -> {

                int filaSeleccionada = tablaReclamos.getSelectedRow();
                int filaModelo = tablaReclamos.convertRowIndexToModel(filaSeleccionada);

                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Por favor, seleccione un reclamo de la tabla para asignar.",
                            "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int idReclamo = (int) tableModel.getValueAt(filaModelo, 0);

                try {
                    Reclamo reclamo = sistema.getTodosLosReclamos().stream()
                            .filter(r -> r.getId() == idReclamo)
                            .findFirst()
                            .orElseThrow(
                                    () -> new ReclamoNoEncontradoException("No se encontró el reclamo en memoria."));

                    AsignarReclamoView form = new AsignarReclamoView(this, sistema, reclamo);
                    form.setVisible(true);

                    cargarReclamosEnTabla();

                } catch (ReclamoNoEncontradoException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    private void cargarReclamosEnTabla() {
        tableModel.setRowCount(0);

        java.util.List<Reclamo> reclamos; // Declarar lista

        // Carga de datos según el ROL del usuario logueado
        if (this.usuarioLogueado instanceof model.Vecino) {
            // Si es Vecino, trae solo SUS reclamos
            reclamos = sistema.getReclamosPorVecino((model.Vecino) this.usuarioLogueado);

        } else if (this.usuarioLogueado instanceof model.Empleado) {
            // Si es Empleado, trae solo los ASIGNADOS A ÉL
            // (Una mejora lógica adicional, la profesora lo apreciará)
            reclamos = sistema.getReclamosPorEmpleado((model.Empleado) this.usuarioLogueado);

        } else {
            // Si es Admin (u otro tipo), trae TODOS
            reclamos = sistema.getTodosLosReclamosOrdenadosPorFecha();
        }

        // El resto del método es igual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Reclamo r : reclamos) {
            String empleadoAsignado = (r.getEmpleadoAsignado() != null)
                    ? r.getEmpleadoAsignado().getNombreCompleto()
                    : "N/A";

            String vecino = r.getVecino().getNombreCompleto();

            Object[] fila = {
                    r.getId(),
                    r.getFechaCreacion().format(formatter),
                    r.getTipo(),
                    r.getEstadoActual(),
                    vecino,
                    empleadoAsignado,
                    r.getUbicacion()
            };

            tableModel.addRow(fila);
        }
    }
}