package Estacionamiento;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AdminFrame extends JFrame {

    public AdminFrame() {
        setTitle("Panel de Administración - Selección de Zona");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 25, 75), getWidth(), getHeight(), new Color(15, 35, 90));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelFondo.setLayout(new BorderLayout());
        add(panelFondo);

        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(60, 0, 20, 0));

        JLabel lblBienvenido = new JLabel("Bienvenido, admin", SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBienvenido.setForeground(Color.WHITE);
        lblBienvenido.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelHeader.add(lblBienvenido);

        panelFondo.add(panelHeader, BorderLayout.NORTH);

        JPanel panelTarjetas = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 50));
        panelTarjetas.setOpaque(false);
        panelFondo.add(panelTarjetas, BorderLayout.CENTER);

        panelTarjetas.add(crearTarjetaZona("Superner", "Zona comercial principal", "super.png"));
        panelTarjetas.add(crearTarjetaZona("Comercial", "Centro comercial", "plaza.png"));
        panelTarjetas.add(crearTarjetaZona("Avenida Concurrida", "Estacionamiento público", "avenida.png"));

        JLabel lblInfo = new JLabel("Como administrador, tienes acceso a todas las zonas", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInfo.setForeground(new Color(150, 150, 150));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panelFondo.add(lblInfo, BorderLayout.SOUTH);
    }

    private JPanel crearTarjetaZona(String titulo, String subtitulo, String nombreImagen) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 15)); // Fondo sutil para la tarjeta
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setPreferredSize(new Dimension(300, 450));
        card.setOpaque(false);
        card.setLayout(null);
        card.setBorder(new LineBorder(new Color(255, 255, 255, 20), 1, true));

        JLabel lblImagen = new JLabel();
        lblImagen.setBounds(100, 40, 100, 100);
        try {
            // Buscamos la imagen en el path 
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/" + nombreImagen));
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            lblImagen.setText("Error Imagen");
            lblImagen.setForeground(Color.RED);
        }
        card.add(lblImagen);

        JLabel lblT = new JLabel(titulo, SwingConstants.CENTER);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblT.setForeground(Color.WHITE);
        lblT.setBounds(20, 160, 260, 35);
        card.add(lblT);

        JLabel lblS = new JLabel(subtitulo, SwingConstants.CENTER);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblS.setForeground(new Color(180, 180, 180));
        lblS.setBounds(20, 200, 260, 20);
        card.add(lblS);

        JLabel btnEspacios = new JLabel("18 espacios", SwingConstants.CENTER);
        btnEspacios.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEspacios.setForeground(Color.WHITE);
        btnEspacios.setOpaque(true);
        btnEspacios.setBackground(new Color(255, 255, 255, 30));
        btnEspacios.setBounds(100, 250, 100, 30);
        card.add(btnEspacios);

        JButton btnAcceder = new JButton("Acceder →");
        btnAcceder.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAcceder.setForeground(Color.WHITE);
        btnAcceder.setContentAreaFilled(false);
        btnAcceder.setBorderPainted(false);
        btnAcceder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcceder.setBounds(80, 320, 140, 40);
        card.add(btnAcceder);

        btnAcceder.addActionListener(e -> {
            new MainDashboard("admin", titulo).setVisible(true); // "titulo" es el nombre de la zona
            dispose();
        });

        return card;
    }
}
