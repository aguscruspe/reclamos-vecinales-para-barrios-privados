package vista;

import excepts.PersistenciaException;
import excepts.UsuarioNoValidoException;
import negocio.SistemaGestionReclamos;

import javax.swing.*;
import java.awt.*;

public class RegistroView extends JDialog {

    private SistemaGestionReclamos sistema;

    // Componentes del formulario
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombreCompleto;
    private JTextField txtDni;
    private JTextField txtDireccion;
    private JButton btnRegistrar;

    public RegistroView(Frame parent, SistemaGestionReclamos sistema) {
        super(parent, "Registro de Nuevo Vecino", true); // true = MODAL
        this.sistema = sistema;

        // --- Configuración de la Ventana (JDialog) ---
        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario (Centro) ---
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

        panelFormulario.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Botón (Sur) ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRegistrar = new JButton("Confirmar Registro");
        panelBoton.add(btnRegistrar);
        add(panelBoton, BorderLayout.SOUTH);

        // --- Acción del Botón Guardar ---
        btnRegistrar.addActionListener(e -> {
            handleRegistrarVecino();
        });
    }

    private void handleRegistrarVecino() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String nombre = txtNombreCompleto.getText();
        String dni = txtDni.getText();
        String direccion = txtDireccion.getText();

        try {
            // Llamamos a la nueva función en la fachada
            sistema.registrarVecino(username, password, nombre, dni, direccion);

            JOptionPane.showMessageDialog(this,
                    "¡Registro exitoso! Ya puede iniciar sesión.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            this.dispose(); // Cierra la ventana de registro

        } catch (UsuarioNoValidoException ex) {
            // Captura errores de validación (datos incompletos, username duplicado)
            JOptionPane.showMessageDialog(this,
                    "Error de Validación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (PersistenciaException ex) {
            // Captura errores de guardado
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el usuario: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}