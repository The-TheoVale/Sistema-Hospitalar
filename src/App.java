import javax.swing.SwingUtilities;

import clinica.ui.LoginClinicaFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginClinicaFrame().setVisible(true));
    }
}