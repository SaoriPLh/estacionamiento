package Estacionamiento;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    public LoginFrame() {
        setTitle("Sistema de Estacionamiento - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 25, 75), getWidth(), getHeight(), new Color(20, 50, 140));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelFondo.setLayout(new GridBagLayout());
        add(panelFondo);
        
        JPanel tarjeta = new JPanel();
        tarjeta.setPreferredSize(new Dimension(380, 550));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(null);
        
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelFondo.add(tarjeta);

        //LOGO AZUL "P"
        JPanel panelLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(13, 110, 253)); // Azul del logo
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, 30));
                g.drawString("P", 18, 35);
            }
        };
        panelLogo.setBounds(160, 30, 60, 50);
        panelLogo.setOpaque(false);
        tarjeta.add(panelLogo);

        JLabel lblTitulo = new JLabel("Sistema de Estacionamiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41));
        lblTitulo.setBounds(20, 95, 340, 30);
        tarjeta.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Gestión Inteligente de Espacios", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(108, 117, 125));
        lblSubtitulo.setBounds(20, 120, 340, 20);
        tarjeta.add(lblSubtitulo);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setBounds(35, 170, 100, 20);
        tarjeta.add(lblUser);

        txtUsuario = new JTextField("");
        txtUsuario.setBounds(35, 195, 310, 40);
        txtUsuario.setForeground(Color.LIGHT_GRAY);
        txtUsuario.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        tarjeta.add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setBounds(35, 250, 100, 20);
        tarjeta.add(lblPass);

        txtContrasena = new JPasswordField("");
        txtContrasena.setBounds(35, 275, 310, 40);
        txtContrasena.setForeground(Color.LIGHT_GRAY);
        txtContrasena.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        tarjeta.add(txtContrasena);

        JButton btnLogin = new JButton("→ Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(13, 110, 253));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(35, 345, 310, 45);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        tarjeta.add(btnLogin);

        JPanel panelDemo = new JPanel();
        panelDemo.setBackground(new Color(248, 249, 250));
        panelDemo.setBounds(35, 410, 310, 110);
        panelDemo.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelDemo.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        tarjeta.add(panelDemo);

        String htmlText = "<html><font color='#495057' size='3'><b>Usuarios de Demo:</b></font><br>"
                + "<font color='#6c757d' size='2'>• admin/admin - Administrador<br>"
                + "• operador1/op1 - Operador (Superner)<br>"
                + "• operador2/op2 - Operador (Comercial)<br>"
                + "• operador3/op3 - Operador (Avenida)</font></html>";
        
        JLabel lblDemo = new JLabel(htmlText);
        panelDemo.add(lblDemo);

btnLogin.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String user = txtUsuario.getText();
        String pass = new String(txtContrasena.getPassword());

        // 1. Verificación para Administrador
        if (user.equals("admin") && pass.equals("admin")) {
            // Enviamos al panel de selección de zona que ya configuramos
            new AdminFrame().setVisible(true); 
            dispose();
        } 
        // 2. Verificación para Operadores (Van directo al Dashboard de su zona)
        else if (user.equals("operador1") && pass.equals("op1")) {
            new MainDashboard(user, "Superner").setVisible(true);
            dispose();
        } 
        else if (user.equals("operador2") && pass.equals("op2")) {
            new MainDashboard(user, "Comercial").setVisible(true);
            dispose();
        } 
        else if (user.equals("operador3") && pass.equals("op3")) {
            new MainDashboard(user, "Avenida Concurrida").setVisible(true);
            dispose();
        } 
        else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }
        });
    }
}