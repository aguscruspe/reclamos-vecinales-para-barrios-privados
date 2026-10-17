package vista;

import negocio.ReclamoController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private ReclamoController sistema;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrarse;
    private JLabel lblError;

    public LoginView(ReclamoController sistema) {
        this.sistema = sistema;

        setTitle("Reclamos del Barrio Privado");
        setSize(400, 250); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));


        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panelFormulario.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelFormulario.add(txtUsername);

        panelFormulario.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelFormulario.add(txtPassword);

        btnLogin = new JButton("Login");
        panelFormulario.add(btnLogin);

        btnRegistrarse = new JButton("Registrarse"); 
        panelFormulario.add(btnRegistrarse); 

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        panelFormulario.add(lblError);
        panelFormulario.add(new JLabel("")); 

        add(panelFormulario, BorderLayout.CENTER);

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
        String nombreUsuario = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (sistema.login(nombreUsuario, password)) {
            this.dispose();

            DashboardView dashboard = new DashboardView(sistema);
            dashboard.setVisible(true);

        } else {
            lblError.setText("Login fallido. Credenciales incorrectas.");
        }
    }

    private void handleRegistro() {
        RegistroView registroDialog = new RegistroView(this, sistema);
        registroDialog.setVisible(true);
    }
}