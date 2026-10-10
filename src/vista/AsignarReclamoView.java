package vista;

import excepts.PersistenciaException;
import excepts.ReclamoNoEncontradoException;
import excepts.UsuarioNoValidoException;
import model.EmpleadoMunicipal;
import model.Reclamo;
import model.Usuario;
import negocio.SistemaGestionReclamos;

import javax.swing.*;
import java.awt.*;

public class AsignarReclamoView extends JDialog {

    private SistemaGestionReclamos sistema;
    private Reclamo reclamoAAsignar;

    // Componentes del formulario
    private JComboBox<String> comboBoxEmpleado;
    private JButton btnAsignar;

    public AsignarReclamoView(JFrame parent, SistemaGestionReclamos sistema, Reclamo reclamo) {
        super(parent, "Asignar Reclamo (ID: " + reclamo.getId() + ")", true);

        this.sistema = sistema;
        this.reclamoAAsignar = reclamo;

        // --- Configuración de la Ventana (JDialog) ---
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario (Centro) ---
        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Obtenemos los nombres completos de todos los Empleados Municipales
        String[] nombresCompletosEmpleados = sistema.getTodosLosUsuarios().values().stream()
                .filter(u -> u instanceof EmpleadoMunicipal)
                .map(Usuario::getNombreCompleto)
                .toArray(String[]::new);

        panelFormulario.add(new JLabel("Asignar a Empleado:"));

        // Creamos el ComboBox con los nombres
        comboBoxEmpleado = new JComboBox<>(nombresCompletosEmpleados);

        // 2. Si ya está asignado, seleccionamos el nombre completo del empleado actual
        if (reclamo.getEmpleadoAsignado() != null) {
            comboBoxEmpleado.setSelectedItem(reclamo.getEmpleadoAsignado().getNombreCompleto());
        }

        panelFormulario.add(comboBoxEmpleado);
        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Botón (Sur) ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAsignar = new JButton("Confirmar Asignación");
        panelBoton.add(btnAsignar);
        add(panelBoton, BorderLayout.SOUTH);

        // --- Acción del Botón Asignar ---
        btnAsignar.addActionListener(e -> {
            handleAsignarReclamo();
        });
    }

    private void handleAsignarReclamo() {
        String nombreCompletoEmpleadoSeleccionado = (String) comboBoxEmpleado.getSelectedItem();

        if (nombreCompletoEmpleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Buscamos el USERNAME asociado al nombre completo (para llamar al Backend)
            String usernameEmpleado = sistema.getTodosLosUsuarios().values().stream()
                    .filter(u -> u instanceof EmpleadoMunicipal)
                    .filter(u -> u.getNombreCompleto().equals(nombreCompletoEmpleadoSeleccionado))
                    .map(Usuario::getUsername)
                    .findFirst()
                    .orElseThrow(() -> new UsuarioNoValidoException(
                            "Error interno: No se pudo encontrar el username del empleado."));

            // 2. Llama a la lógica de negocio (Backend) con el USERNAME
            sistema.asignarReclamo(reclamoAAsignar.getId(), usernameEmpleado);

            JOptionPane.showMessageDialog(this,
                    "Reclamo asignado a " + nombreCompletoEmpleadoSeleccionado + " con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (UsuarioNoValidoException | ReclamoNoEncontradoException | PersistenciaException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al asignar el reclamo: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}