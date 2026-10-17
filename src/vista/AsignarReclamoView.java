package vista;

import excepts.AsignacionInvalidaException;
import excepts.PersistenciaException;
import excepts.ReclamoNoEncontradoException;
import excepts.UsuarioNoValidoException;
import model.Empleado;
import model.Reclamo;
import negocio.ReclamoController;

import javax.swing.*;
import java.awt.*;

public class AsignarReclamoView extends JDialog {

    private ReclamoController sistema;
    private Reclamo reclamoAAsignar;

    private JComboBox<Empleado> comboBoxEmpleado;
    private JButton btnAsignar;

    public AsignarReclamoView(JFrame parent, ReclamoController sistema, Reclamo reclamo) {
        super(parent, "Asignar Reclamo (ID: " + reclamo.getId() + ")", true);

        this.sistema = sistema;
        this.reclamoAAsignar = reclamo;

        setSize(400, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Empleado[] empleadosDelArea = sistema.getTodosLosUsuarios().values().stream()
                .filter(u -> u instanceof Empleado)
                .map(u -> (Empleado) u)
                .filter(empleado -> empleado.getArea() == reclamo.getTipo().getArea())
                .toArray(Empleado[]::new);

        panelFormulario.add(new JLabel("Empleado (" + reclamo.getTipo().getArea() + "):"));

        comboBoxEmpleado = new JComboBox<>(empleadosDelArea);
        comboBoxEmpleado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Empleado empleado = (Empleado) value;
                if (empleado != null) {
                    setText(empleado.getNombreCompleto() + " (" + empleado.getNombreUsuario() + ")");
                }
                return this;
            }
        });

        if (reclamo.getEmpleadoAsignado() != null) {
            comboBoxEmpleado.setSelectedItem(reclamo.getEmpleadoAsignado());
        }

        panelFormulario.add(comboBoxEmpleado);
        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAsignar = new JButton("Confirmar Asignación");
        panelBoton.add(btnAsignar);
        add(panelBoton, BorderLayout.SOUTH);

        btnAsignar.addActionListener(e -> {
            handleAsignarReclamo();
        });
    }

    private void handleAsignarReclamo() {
        Empleado empleadoSeleccionado = (Empleado) comboBoxEmpleado.getSelectedItem();

        if (empleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            sistema.asignarReclamo(reclamoAAsignar.getId(), empleadoSeleccionado.getNombreUsuario());

            JOptionPane.showMessageDialog(this,
                    "Reclamo asignado a " + empleadoSeleccionado.getNombreCompleto() + " con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (UsuarioNoValidoException | ReclamoNoEncontradoException | PersistenciaException
                 | AsignacionInvalidaException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al asignar el reclamo: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}