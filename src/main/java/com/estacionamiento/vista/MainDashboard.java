package Estacionamiento;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainDashboard extends JFrame {

    private JPanel panelContenedor;
    private CardLayout cardLayout;
    private JPanel panelListaHistorial;
    private JPanel[] cajonesReferencia = new JPanel[19];
    private JLabel lblDisponiblesCount, lblOcupadosCount;

    public MainDashboard(String usuario, String zona) {
        setTitle("Parking Manager - " + zona);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        add(crearSidebar(usuario), BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelContenedor.add(crearPanelDashboard(zona), "VistaDashboard");
        panelContenedor.add(crearPanelHistorial(), "VistaHistorial");

        add(panelContenedor, BorderLayout.CENTER);
        actualizarContadores();
    }

    private JPanel crearSidebar(String usuario) {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(23, 27, 48)); 
        sidebar.setPreferredSize(new Dimension(260, getHeight()));

        JLabel lblIconUser = new JLabel("O");
        lblIconUser.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        lblIconUser.setForeground(Color.WHITE);
        lblIconUser.setBounds(25, 25, 45, 45);
        sidebar.add(lblIconUser);

        JLabel lblUser = new JLabel(usuario);
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUser.setBounds(80, 25, 150, 25);
        sidebar.add(lblUser);

        JLabel lblRole = new JLabel("Usuario");
        lblRole.setForeground(Color.GRAY);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setBounds(80, 48, 100, 20);
        sidebar.add(lblRole);
    
        String[] items = {"Dashboard", "Historial", "Administración", "Configuración"};
        String[] iconos = {"O", "O", "O", "O"}; 

        int y = 130;
        for (int i = 0; i < items.length; i++) {
            String texto = items[i];
            String icono = iconos[i];

            JButton btn = new JButton(icono + "  " + texto);
            btn.setBounds(15, y, 230, 50);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (texto.equals("Dashboard")) {
                btn.setBackground(new Color(13, 110, 253));
                btn.setForeground(Color.WHITE);
                btn.setContentAreaFilled(true);
            } else {
                btn.setContentAreaFilled(false);
                btn.setForeground(new Color(200, 200, 200));
            }

            btn.addActionListener(e -> {
                cardLayout.show(panelContenedor, "Vista" + texto);
                for (Component c : sidebar.getComponents()) {
                    if (c instanceof JButton && !((JButton) c).getText().contains("Cerrar")) {
                        ((JButton) c).setContentAreaFilled(false);
                        c.setForeground(new Color(200, 200, 200));
                    }
                }
                btn.setContentAreaFilled(true);
                btn.setBackground(new Color(13, 110, 253));
                btn.setForeground(Color.WHITE);
            });

            sidebar.add(btn);
            y += 60;
        }

        JButton btnLogout = new JButton("↳  Cerrar Sesión");
        btnLogout.setBounds(15, 700, 230, 50); 
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogout.setForeground(new Color(255, 99, 99));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        sidebar.add(btnLogout);
        return sidebar;
    }

    private JPanel crearPanelDashboard(String zona) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);

        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1000, 220));
        header.setBackground(Color.WHITE);

        JLabel t = new JLabel("Panel de Control");
        t.setFont(new Font("Segoe UI", Font.BOLD, 28));
        t.setBounds(30, 20, 300, 40);
        header.add(t);

        JButton btnReg = new JButton("+ Registrar Entrada");
        btnReg.setBounds(820, 30, 200, 45);
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReg.setBackground(new Color(13, 110, 253));
        btnReg.setForeground(Color.WHITE);
        btnReg.setFocusPainted(false);
        btnReg.setBorderPainted(false);
        btnReg.setOpaque(true);
        btnReg.setContentAreaFilled(true);
        btnReg.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReg.addActionListener(e -> {
            DialogRegistro dr = new DialogRegistro(this);
            dr.setVisible(true);
        });

        header.add(btnReg);

        JPanel pnlResumen = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlResumen.setBounds(30, 100, 980, 100);
        pnlResumen.setBackground(Color.WHITE);

        pnlResumen.add(crearTarjetaResumen("Total Espacios", "18", new Color(240, 245, 255), ""));

        JPanel pnlDisp = crearTarjetaResumen("Disponibles", "18", new Color(235, 250, 240), "️");
        lblDisponiblesCount = (JLabel) pnlDisp.getComponent(2);
        pnlResumen.add(pnlDisp);

        JPanel pnlOcup = crearTarjetaResumen("Ocupados", "0", new Color(255, 240, 240), "️");
        lblOcupadosCount = (JLabel) pnlOcup.getComponent(2);
        pnlResumen.add(pnlOcup);

        header.add(pnlResumen);
        pnl.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 6, 15, 15));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        for (int i = 1; i <= 18; i++) {
            cajonesReferencia[i] = crearCajonIndividual(i);
            grid.add(cajonesReferencia[i]);
        }
        pnl.add(new JScrollPane(grid), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel crearCajonIndividual(int n) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 252, 248));
        p.setBorder(new LineBorder(new Color(40, 167, 69, 80), 2, true));
        p.add(new JLabel(" " + n), BorderLayout.NORTH);

        ImageIcon iconCarro = null;
        try {
            ImageIcon original = new ImageIcon(getClass().getResource("/carro.png"));
            Image escalada = original.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            iconCarro = new ImageIcon(escalada);
        } catch (Exception e) {
            System.out.println("Error cargando carro.png");
        }

        JLabel ico = new JLabel(iconCarro, SwingConstants.CENTER);
        if (iconCarro == null) { // Fallback si no encuentra la imagen
            ico.setText("🚗");
            ico.setFont(new Font("Segoe UI", Font.PLAIN, 35));
            ico.setForeground(new Color(40, 167, 69));
        }
        
        p.add(ico, BorderLayout.CENTER);

        JLabel est = new JLabel("Libre", SwingConstants.CENTER);
        est.setOpaque(true);
        est.setBackground(new Color(40, 167, 69, 40));
        p.add(est, BorderLayout.SOUTH);
        return p;
    }

    public void actualizarEstadoCajon(int num, String tipo, String placa, String hora) {
        if (num < 1 || num > 18) return;
        
        JPanel cajon = cajonesReferencia[num];
        // El componente 1 es el icono (Label con imagen)
        JLabel lblIco = (JLabel) cajon.getComponent(1);
        // El componente 2 es el estado ("Libre"/"Ocupado")
        JLabel lblEst = (JLabel) cajon.getComponent(2);

        if (tipo.equals("VIP")) {
            cajon.setBackground(new Color(255, 249, 219));
            cajon.setBorder(new LineBorder(new Color(255, 193, 7), 2, true));
        } else {
            cajon.setBackground(new Color(255, 240, 240));
            cajon.setBorder(new LineBorder(new Color(220, 53, 69, 80), 2, true));
        }
        
        lblEst.setText("Ocupado");
        lblEst.setBackground(new Color(0, 0, 0, 15));

        agregarAlHistorial(placa, "Cajón " + num, hora, tipo.equals("VIP"));
        actualizarContadores();
    }

    private void agregarAlHistorial(String p, String e, String h, boolean v) {
        JPanel card = new JPanel(null);
        card.setMaximumSize(new Dimension(1000, 70));
        card.setPreferredSize(new Dimension(1000, 70));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        JLabel info = new JLabel("<html><b>" + p + "</b> - " + e + (v ? " <font color='orange'>VIP</font>" : "") + "<br><font color='gray'>Ingreso: " + h + "</font></html>");
        info.setBounds(20, 10, 400, 50);
        card.add(info);
        panelListaHistorial.add(card);
        panelListaHistorial.add(Box.createRigidArea(new Dimension(0, 5)));
        panelListaHistorial.revalidate();
    }

    private void actualizarContadores() {
        int o = 0;
        for (int i = 1; i <= 18; i++) {
            if (((JLabel) cajonesReferencia[i].getComponent(2)).getText().equals("Ocupado")) {
                o++;
            }
        }
        if (lblDisponiblesCount != null) {
            lblDisponiblesCount.setText(String.valueOf(18 - o));
            lblOcupadosCount.setText(String.valueOf(o));
        }
    }

    private JPanel crearTarjetaResumen(String tit, String val, Color bg, String ico) {
        JPanel card = new JPanel(null);
        card.setBackground(bg);
        card.setBorder(new LineBorder(bg.darker(), 1, true));
        JLabel lIco = new JLabel(ico, SwingConstants.CENTER);
        lIco.setBounds(15, 25, 40, 40);
        card.add(lIco);
        JLabel lTit = new JLabel(tit);
        lTit.setBounds(70, 20, 150, 20);
        lTit.setForeground(Color.GRAY);
        card.add(lTit);
        JLabel lVal = new JLabel(val);
        lVal.setBounds(70, 40, 100, 35);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        card.add(lVal);
        return card;
    }

    private JPanel crearPanelHistorial() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 249, 252));
        JLabel h = new JLabel("Historial de Actividad");
        h.setFont(new Font("Segoe UI", Font.BOLD, 26));
        h.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 0));
        p.add(h, BorderLayout.NORTH);

        panelListaHistorial = new JPanel();
        panelListaHistorial.setLayout(new BoxLayout(panelListaHistorial, BoxLayout.Y_AXIS));
        panelListaHistorial.setBackground(new Color(248, 249, 252));
        p.add(new JScrollPane(panelListaHistorial), BorderLayout.CENTER);
        return p;
    }
}