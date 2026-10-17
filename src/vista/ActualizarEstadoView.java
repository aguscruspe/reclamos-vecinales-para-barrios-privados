package vista;

import excepts.PersistenciaException;
import excepts.ReclamoNoEncontradoException;
import excepts.UsuarioNoValidoException;
import model.Reclamo;
import model.enums.EstadoReclamo;
import negocio.ReclamoController;

import javax.swing.*;
import java.awt.*;

public class ActualizarEstadoView extends JDialog {

    private ReclamoController sistema;
    private Reclamo reclamoAActualizar;

    private JComboBox<EstadoReclamo> comboBoxEstado;
    private JTextField txtObservacion;
    private JButton btnGuardar;

    public ActualizarEstadoView(JFrame parent, ReclamoController sistema, Reclamo reclamo) {
        super(parent, "Actualizar Estado Reclamo (ID: " + reclamo.getId() + ")", true);

        this.sistema = sistema;
        this.reclamoAActualizar = reclamo;

        setSize(450, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelFormulario.add(new JLabel("Nuevo Estado:"));
        comboBoxEstado = new JComboBox<>();
        comboBoxEstado.setModel(new DefaultComboBoxModel<>(EstadoReclamo.values()));
        
        comboBoxEstado.setSelectedItem(reclamo.getEstadoActual());
        panelFormulario.add(comboBoxEstado);

        panelFormulario.add(new JLabel("Observación:"));
        txtObservacion = new JTextField();
        panelFormulario.add(txtObservacion);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnGuardar = new JButton("Actualizar Estado");
        panelBoton.add(btnGuardar);
        add(panelBoton, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> {
            handleActualizarEstado();
        });
    }

    private void handleActualizarEstado() {
        EstadoReclamo nuevoEstado = (EstadoReclamo) comboBoxEstado.getSelectedItem();
        String observacion = txtObservacion.getText();

        if (observacion.trim().isEmpty()) {
            observacion = "Actualizado por " + sistema.getUsuarioLogueado().getNombreUsuario();
        }

        try {
            sistema.actualizarEstadoReclamo(
                    reclamoAActualizar.getId(),
                    nuevoEstado,
                    observacion
            );

            JOptionPane.showMessageDialog(this,
                    "Reclamo actualizado con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (UsuarioNoValidoException | PersistenciaException | ReclamoNoEncontradoException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar el reclamo: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}