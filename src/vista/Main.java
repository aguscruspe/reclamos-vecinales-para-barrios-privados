package vista;

import excepts.PersistenciaException;
import negocio.ReclamoController;
import repo.GestorPersistencia;
import repo.IRepositorio;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        IRepositorio repositorio = new GestorPersistencia();
        ReclamoController sistema = new ReclamoController(repositorio);

        try {
            sistema.iniciarSistema();
            sistema.crearDatosDePruebaSiNoExisten();

        } catch (PersistenciaException e) {
            System.err.println("ERROR FATAL DE PERSISTENCIA: " + e.getMessage());
            return; // No continuamos si la persistencia falla
        } catch (Exception e) {
            System.err.println("ERROR INESPERADO: " + e.getMessage());
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginView login = new LoginView(sistema);
                login.setVisible(true);
            }
        });
    }
}