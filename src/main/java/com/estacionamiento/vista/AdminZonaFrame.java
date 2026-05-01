package Estacionamiento;

import javax.swing.*;
import java.awt.*;

public class AdminZonaFrame extends JFrame {

    private String zona;

    public AdminZonaFrame(String zona) {
        this.zona = zona;

        setTitle("Panel de Control Admin - Zona: " + zona);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(new Color(26, 32, 53));
        panelMenu.setPreferredSize(new Dimension(220, 0));
        panelMenu.setLayout(null);
        add(panelMenu, BorderLayout.WEST);

        JLabel lblIconoUser = new JLabel("A", SwingConstants.CENTER);
        lblIconoUser.setFont(new Font("Serif", Font.BOLD, 30));
        lblIconoUser.setForeground(Color.WHITE);
        lblIconoUser.setBackground(new Color(255, 165, 0));
        lblIconoUser.setOpaque(true);
        lblIconoUser.setBounds(20, 30, 50, 50);
        panelMenu.add(lblIconoUser);

        JLabel lblRol = new JLabel("Administrador");
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRol.setForeground(new Color(150, 150, 150));
        lblRol.setBounds(80, 35, 120, 20);
        panelMenu.add(lblRol);

        JLabel lblName = new JLabel("admin");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(80, 55, 120, 20);
        panelMenu.add(lblName);

        int btnY = 120;
        crearBotonMenu(panelMenu, "Panel Principal", btnY, false);
        btnY += 50;
        crearBotonMenu(panelMenu, "Historial Global", btnY, false);
        btnY += 50;
        crearBotonMenu(panelMenu, "$ Administración", btnY, false);
        btnY += 50;
        crearBotonMenu(panelMenu, "Configuración", btnY, false);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(50, 50, 70));
        btnVolver.setBorderPainted(false);
        btnVolver.setBounds(10, 620, 200, 30);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelMenu.add(btnVolver);
        btnVolver.addActionListener(e -> {
            new AdminFrame().setVisible(true);
            dispose();
        });

        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(new Color(244, 247, 252));
        panelContenido.setLayout(new BorderLayout());
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(panelContenido, BorderLayout.CENTER);

        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelContenido.add(panelHeader, BorderLayout.NORTH);

        JLabel lblTituloPanel = new JLabel("Visualización en Tiempo Real");
        lblTituloPanel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panelHeader.add(lblTituloPanel, BorderLayout.WEST);

        JLabel lblSubZona = new JLabel("Zona: " + zona + " (Modo Admin)");
        lblSubZona.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubZona.setForeground(Color.GRAY);
        panelHeader.add(lblSubZona, BorderLayout.SOUTH);

        JPanel panelGrid = new JPanel();
        panelGrid.setOpaque(false);
        panelGrid.setLayout(new GridLayout(3, 6, 15, 15));
        panelGrid.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        panelContenido.add(panelGrid, BorderLayout.CENTER);


        // Generar 18 espacios de ejemplo
        for (int i = 1; i <= 18; i++) {
            boolean ocupado = false;
            if (i == 1 || i == 7 || i == 8 || i == 15) {
                ocupado = true;
            }

        }
    }

    private void crearBotonMenu(JPanel panel, String texto, int y, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBounds(0, y, 220, 40);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(180, 180, 190));
        btn.setBackground(new Color(26, 32, 53));
        panel.add(btn);
    }
}
