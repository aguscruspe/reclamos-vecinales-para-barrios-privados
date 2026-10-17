package vista;

import excepts.PersistenciaException;
import excepts.UsuarioNoValidoException;
import model.Lote;
import negocio.ReclamoController;

import javax.swing.*;
import java.awt.*;

public class RegistroView extends JDialog {

    private ReclamoController sistema;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombreCompleto;
    private JTextField txtDni;
    private JComboBox<Lote> comboBoxLote;
    private JButton btnRegistrar;

    public RegistroView(Frame parent, ReclamoController sistema) {
        super(parent, "Registro de Nuevo Vecino", true); 
        this.sistema = sistema;

        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelFormulario.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelFormulario.add(txtUsername);

        panelFormulario.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelFormulario.add(txtPassword);

        panelFormulario.add(new JLabel("Nombre Completo:"));
        txtNombreCompleto = new JTextField();
        panelFormulario.add(txtNombreCompleto);

        panelFormulario.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Lote:"));
        comboBoxLote = new JComboBox<>(sistema.getLotes().toArray(new Lote[0]));
        panelFormulario.add(comboBoxLote);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRegistrar = new JButton("Confirmar Registro");
        panelBoton.add(btnRegistrar);
        add(panelBoton, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> {
            handleRegistrarVecino();
        });
    }

    private void handleRegistrarVecino() {
        String nombreUsuario = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String nombre = txtNombreCompleto.getText();
        String dni = txtDni.getText();
        Lote lote = (Lote) comboBoxLote.getSelectedItem();

        try {
            sistema.registrarVecino(nombreUsuario, password, nombre, dni, lote);

            JOptionPane.showMessageDialog(this,
                    "¡Registro exitoso! Ya puede iniciar sesión.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (UsuarioNoValidoException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de Validación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el usuario: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}