package vista;

import negocio.SistemaGestionReclamos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private SistemaGestionReclamos sistema;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrarse; // <-- NUEVO BOTÓN
    private JLabel lblError;

    public LoginView(SistemaGestionReclamos sistema) {
        this.sistema = sistema;

        // --- Configuración de la Ventana (JFrame) ---
        setTitle("Sistema de Reclamos");
        setSize(400, 250); // Ligeramente más alto para el nuevo botón
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario (Centro) ---
        // Ajustado a 4 filas para el nuevo botón (hgap, vgap)
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10)); // <-- MODIFICADO
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panelFormulario.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelFormulario.add(txtUsername);

        panelFormulario.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelFormulario.add(txtPassword);

        // Fila de botones
        btnLogin = new JButton("Login");
        panelFormulario.add(btnLogin);

        btnRegistrarse = new JButton("Registrarse"); // <-- NUEVO BOTÓN
        panelFormulario.add(btnRegistrarse); // <-- NUEVO BOTÓN

        // Fila de error (ahora dentro del grid)
        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormulario.add(lblError);
        // Ocupa 2 columnas
        panelFormulario.add(new JLabel("")); // Placeholder

        // --- Agregar panel a la ventana ---
        add(panelFormulario, BorderLayout.CENTER);

        // --- Manejo de Eventos ---
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistro();
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (sistema.login(username, password)) {
            // Login exitoso
            this.dispose();

            // Abrimos el Dashboard
            DashboardView dashboard = new DashboardView(sistema);
            dashboard.setVisible(true);

        } else {
            // Login fallido
            lblError.setText("Login fallido. Credenciales incorrectas.");
        }
    }

    private void handleRegistro() {
        // 'this' es el JFrame de Login, que actúa como 'parent'
        RegistroView registroDialog = new RegistroView(this, sistema);
        registroDialog.setVisible(true);
        // El código se pausa aquí hasta que el JDialog modal se cierre
    }
}