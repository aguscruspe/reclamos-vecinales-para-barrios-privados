package vista;

import model.Empleado;
import model.Intendente;
import model.Reclamo;
import model.Usuario;
import model.Vecino;
import negocio.ReclamoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends JFrame {

    private static final int COLUMNA_REPORTADO_POR = 4;

    private ReclamoController sistema;
    private Usuario usuarioLogueado;

    private JTable tablaReclamos;
    private DefaultTableModel tableModel;

    public DashboardView(ReclamoController sistema) {
        this.sistema = sistema;
        this.usuarioLogueado = sistema.getUsuarioLogueado();

        configurarVentana();
        add(crearTitulo(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
        cargarReclamosEnTabla();
    }

    private void configurarVentana() {
        setTitle("Reclamos del Barrio - Panel (" + usuarioLogueado.getNombreUsuario() + ")");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private JLabel crearTitulo() {
        JLabel lblTitulo = new JLabel("Bienvenido, " + usuarioLogueado.getNombreCompleto(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return lblTitulo;
    }

    private JScrollPane crearTabla() {
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

        if (usuarioLogueado instanceof Vecino) {
            TableColumn columnaReportadoPor = tablaReclamos.getColumnModel().getColumn(COLUMNA_REPORTADO_POR);
            columnaReportadoPor.setMinWidth(0);
            columnaReportadoPor.setMaxWidth(0);
            columnaReportadoPor.setPreferredWidth(0);
        }

        JScrollPane scrollPane = new JScrollPane(tablaReclamos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return scrollPane;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevoReclamo = new JButton("Crear Nuevo Reclamo");
        JButton btnAsignar = new JButton("Asignar Reclamo Seleccionado");
        JButton btnActualizarEstado = new JButton("Actualizar Estado Seleccionado");

        btnNuevoReclamo.addActionListener(e -> abrirCrearReclamo());
        btnAsignar.addActionListener(e -> abrirAsignar());
        btnActualizarEstado.addActionListener(e -> abrirActualizarEstado());

        if (usuarioLogueado instanceof Vecino) {
            panelBotones.add(btnNuevoReclamo);
        } else if (usuarioLogueado instanceof Intendente) {
            panelBotones.add(btnAsignar);
            panelBotones.add(btnActualizarEstado);
        } else if (usuarioLogueado instanceof Empleado) {
            panelBotones.add(btnActualizarEstado);
        }
        return panelBotones;
    }

    private void abrirCrearReclamo() {
        new CrearReclamoView(this, sistema).setVisible(true);
        cargarReclamosEnTabla();
    }

    private void abrirAsignar() {
        Reclamo reclamo = obtenerReclamoSeleccionado();
        if (reclamo == null) return;
        new AsignarReclamoView(this, sistema, reclamo).setVisible(true);
        cargarReclamosEnTabla();
    }

    private void abrirActualizarEstado() {
        Reclamo reclamo = obtenerReclamoSeleccionado();
        if (reclamo == null) return;
        new ActualizarEstadoView(this, sistema, reclamo).setVisible(true);
        cargarReclamosEnTabla();
    }

    private Reclamo obtenerReclamoSeleccionado() {
        int fila = tablaReclamos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un reclamo de la tabla.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int idReclamo = (int) tableModel.getValueAt(tablaReclamos.convertRowIndexToModel(fila), 0);
        return sistema.getTodosLosReclamos().stream()
                .filter(r -> r.getId() == idReclamo)
                .findFirst()
                .orElse(null);
    }

    private void cargarReclamosEnTabla() {
        tableModel.setRowCount(0);

        List<Reclamo> reclamos;
        if (usuarioLogueado instanceof Vecino) {
            reclamos = sistema.getReclamosPorVecino((Vecino) usuarioLogueado);
        } else if (usuarioLogueado instanceof Empleado) {
            reclamos = sistema.getReclamosPorEmpleado((Empleado) usuarioLogueado);
        } else {
            reclamos = sistema.getTodosLosReclamosOrdenadosPorFecha();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Reclamo reclamo : reclamos) {
            String empleadoAsignado = (reclamo.getEmpleadoAsignado() != null)
                    ? reclamo.getEmpleadoAsignado().getNombreCompleto()
                    : "N/A";

            Object[] fila = {
                    reclamo.getId(),
                    reclamo.getFechaCreacion().format(formatter),
                    reclamo.getTipo(),
                    reclamo.getEstadoActual(),
                    reclamo.getVecino().getNombreCompleto(),
                    empleadoAsignado,
                    reclamo.getUbicacion()
            };
            tableModel.addRow(fila);
        }
    }
}