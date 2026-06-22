import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class roomLogin extends JFrame{

private JTextField tfLogin;
private JTextField tfSenha;
private JButton btnEntrar;

public roomLogin() {
    super("Login");
    inicializarJanela();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800,600);
    setLayout(new BorderLayout());
}

private void inicializarJanela(){

     //Painel de Login (com BoxLayout vertical)
    JPanel painelLogin = new JPanel();
    painelLogin.setLayout(new BoxLayout(painelLogin, BoxLayout.Y_AXIS));

    //Título do topo
    JLabel titulo = new JLabel("Sistema de Clínica Médica", SwingConstants.CENTER);
    titulo.setFont(new Font("Arial", Font.BOLD, 60));
    this.add(titulo, BorderLayout.NORTH);
    
    //Alocando Label's e TextFields
    JLabel labLogin = new JLabel("Login");




}
}