package vista;

import excepts.PersistenciaException;
import negocio.SistemaGestionReclamos;
import repo.GestorPersistencia;
import repo.IRepositorio;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // 1. Inyección de Dependencias (Igual que antes)
        IRepositorio repositorio = new GestorPersistencia();
        SistemaGestionReclamos sistema = new SistemaGestionReclamos(repositorio);

        try {
            // 2. Carga inicial del sistema (Igual que antes)
            sistema.iniciarSistema();
            sistema.crearDatosDePruebaSiNoExisten();

        } catch (PersistenciaException e) {
            System.err.println("ERROR FATAL DE PERSISTENCIA: " + e.getMessage());
            return; // No continuamos si la persistencia falla
        } catch (Exception e) {
            System.err.println("ERROR INESPERADO: " + e.getMessage());
            return;
        }

        // 3. Iniciar la Interfaz Gráfica (GUI)
        // SwingUtilities.invokeLater asegura que la GUI se cree y muestre
        // en el hilo correcto (Event Dispatch Thread).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Creamos la vista de Login y le pasamos el sistema (controlador)
                LoginView login = new LoginView(sistema);
                login.setVisible(true);
            }
        });
    }
}