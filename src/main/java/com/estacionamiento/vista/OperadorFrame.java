package Estacionamiento;

import javax.swing.*;
import java.awt.*;

public class OperadorFrame extends JFrame {

    private String usuario;
    private String zona;

    public OperadorFrame(String usuario, String zona) {
        this.usuario = usuario;
        this.zona = zona;

        setTitle("Panel de Control - Operador: " + usuario + " - Zona: " + zona);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(new Color(26, 32, 53));
        panelMenu.setPreferredSize(new Dimension(220, 0));
        panelMenu.setLayout(null);
        add(panelMenu, BorderLayout.WEST);

        JLabel lblIconoUser = new JLabel("●", SwingConstants.CENTER); 
        lblIconoUser.setFont(new Font("Serif", Font.PLAIN, 40));
        lblIconoUser.setForeground(new Color(58, 134, 255));
        lblIconoUser.setBounds(20, 30, 50, 50);
        panelMenu.add(lblIconoUser);

        JLabel lblRol = new JLabel("Operador");
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRol.setForeground(new Color(150, 150, 150));
        lblRol.setBounds(80, 35, 120, 20);
        panelMenu.add(lblRol);

        JLabel lblName = new JLabel(usuario);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(80, 55, 120, 20);
        panelMenu.add(lblName);

        JSeparator sep = new JSeparator();
        sep.setBounds(10, 100, 200, 1);
        sep.setForeground(new Color(50, 50, 70));
        panelMenu.add(sep);

        int btnY = 120;
        crearBotonMenu(panelMenu, "Dashboard", btnY, true); // Activo
        btnY += 50;
        crearBotonMenu(panelMenu, "Historial", btnY, false);
        btnY += 50;
        crearBotonMenu(panelMenu, "Configuración", btnY, false);

        // Botón Cerrar Sesión (abajo)
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setForeground(new Color(255, 100, 100)); // Rojo suave
        btnLogout.setBackground(new Color(26, 32, 53));
        btnLogout.setBorderPainted(false);
        btnLogout.setBounds(10, 620, 200, 30);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelMenu.add(btnLogout);
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(new Color(244, 247, 252));
        panelContenido.setLayout(new BorderLayout());
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(panelContenido, BorderLayout.CENTER);

        // Header del contenido
        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setLayout(new BorderLayout());
        panelContenido.add(panelHeader, BorderLayout.NORTH);

        JLabel lblTituloPanel = new JLabel("Panel de Control");
        lblTituloPanel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloPanel.setForeground(Color.BLACK);
        panelHeader.add(lblTituloPanel, BorderLayout.WEST);

        JLabel lblSubZona = new JLabel("Zona: " + zona);
        lblSubZona.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubZona.setForeground(Color.GRAY);
        panelHeader.add(lblSubZona, BorderLayout.SOUTH);

        JButton btnRegistrar = new JButton("+ Registrar Entrada");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setForeground(Color.WHITE); 
        btnRegistrar.setBackground(new Color(13, 110, 253)); 

        btnRegistrar.setContentAreaFilled(true); 
        btnRegistrar.setOpaque(true); 
        
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(150, 35));
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelHeader.add(btnRegistrar, BorderLayout.EAST);
        btnRegistrar.addActionListener(e -> mostrarDialogoRegistro());

        JPanel panelGrid = new JPanel();
        panelGrid.setOpaque(false);
        panelGrid.setLayout(new GridLayout(3, 6, 15, 15)); 
        panelGrid.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        panelContenido.add(panelGrid, BorderLayout.CENTER);

    }

    private void crearBotonMenu(JPanel panel, String texto, int y, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBounds(0, y, 220, 40);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        if (activo) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(58, 134, 255)); // Azul activo
        } else {
            btn.setForeground(new Color(180, 180, 190));
            btn.setBackground(new Color(26, 32, 53)); // Fondo menú
        }
        panel.add(btn);
    }

    private void mostrarDialogoRegistro() {
        JDialog dialog = new JDialog(this, "Registrar Entrada", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Matrícula del Vehículo:"), gbc);
        gbc.gridx = 1;
        JTextField txtMatricula = new JTextField(10);
        dialog.add(txtMatricula, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Tipo de Cliente:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Estándar", "VIP"});
        dialog.add(cbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnConfirmar = new JButton("Confirmar Registro");
        btnConfirmar.setBackground(new Color(58, 134, 255));
        btnConfirmar.setForeground(Color.WHITE);
        dialog.add(btnConfirmar, gbc);

        btnConfirmar.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Entrada registrada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }
}
