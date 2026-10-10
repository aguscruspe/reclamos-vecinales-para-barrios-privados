package vista;

import excepts.PersistenciaException;
import excepts.UsuarioNoValidoException;
import model.enums.TipoReclamo;
import negocio.ReclamoController;

import javax.swing.*;
import java.awt.*;

public class CrearReclamoView extends JDialog {

    private ReclamoController sistema;

    // Componentes del formulario
    private JComboBox<TipoReclamo> comboBoxTipo;
    private JTextField txtDescripcion;
    private JTextField txtUbicacion;
    private JButton btnGuardar;

    public CrearReclamoView(JFrame parent, ReclamoController sistema) {
        super(parent, "Crear Nuevo Reclamo", true); // true = MODAL
        this.sistema = sistema;

        // --- Configuración de la Ventana (JDialog) ---
        setSize(450, 200);
        setLocationRelativeTo(parent); // Se centra sobre el Dashboard
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario (Centro) ---
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelFormulario.add(new JLabel("Tipo de Reclamo:"));
        comboBoxTipo = new JComboBox<>();
        comboBoxTipo.setModel(new DefaultComboBoxModel<>(TipoReclamo.values()));
        panelFormulario.add(comboBoxTipo);

        panelFormulario.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Ubicación:"));
        txtUbicacion = new JTextField();
        panelFormulario.add(txtUbicacion);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Botón (Sur) ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnGuardar = new JButton("Guardar Reclamo");
        panelBoton.add(btnGuardar);
        add(panelBoton, BorderLayout.SOUTH);

        // --- Acción del Botón Guardar ---
        btnGuardar.addActionListener(e -> {
            handleGuardarReclamo();
        });
    }

    private void handleGuardarReclamo() {
        TipoReclamo tipo = (TipoReclamo) comboBoxTipo.getSelectedItem();
        String descripcion = txtDescripcion.getText();
        String ubicacion = txtUbicacion.getText();

        if (descripcion.trim().isEmpty() || ubicacion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La descripción y la ubicación no pueden estar vacías.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            sistema.crearReclamo(tipo, descripcion, ubicacion);

            JOptionPane.showMessageDialog(this,
                    "Reclamo creado con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (UsuarioNoValidoException | PersistenciaException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear el reclamo: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}