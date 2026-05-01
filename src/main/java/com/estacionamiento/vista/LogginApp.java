package Esta;

import javax.swing.*;
import java.awt.*;

public class LogginApp extends JFrame {

    private final Color AZUL_OSCURO = new Color(10, 25, 49);
    private final Color AZUL_ACCENTO = new Color(37, 99, 235);
    private final Font FUENTE_UI = new Font("Segoe UI", Font.PLAIN, 14);

    public LogginApp() {
        initWindow();
        
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL_OSCURO, getWidth(), getHeight(), new Color(22, 67, 150)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        mainPanel.add(CrearLogin());
        add(mainPanel);
    }

    private JPanel CrearLogin() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        card.setPreferredSize(new Dimension(380, 520));


        addComp(card, CrearIcono());
        addComp(card, EstiloLabel("Sistema de Estacionamiento", 20, true));
        addComp(card, EstiloLabel("Gestión Inteligente de Espacios", 13, false));
        addComp(card, (JComponent) Box.createVerticalStrut(25));
        
        addComp(card, new JLabel("Usuario"));
        addComp(card, new JTextField());
        addComp(card, (JComponent) Box.createVerticalStrut(10));
        
        addComp(card, new JLabel("Contraseña"));
        addComp(card, new JPasswordField());
        addComp(card, (JComponent) Box.createVerticalStrut(25));
        
        JButton btn = new JButton("Iniciar Sesión");
        EstiloBoton(btn);
        addComp(card, btn);

        return card;
    }

    // --- MÉTODOS DE ESTILO (Aquí es donde reduces código y parece Pro) ---

    private void addComp(JPanel container, JComponent comp) {
        comp.setAlignmentX(Component.CENTER_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        container.add(comp);
        container.add(Box.createVerticalStrut(5));
    }

    private JLabel CrearIcono() {
        JLabel l = new JLabel("P", SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(AZUL_ACCENTO);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 24));
        l.setPreferredSize(new Dimension(50, 50));
        l.setMaximumSize(new Dimension(50, 50));
        return l;
    }

    private JLabel EstiloLabel(String text, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(bold ? new Color(30, 41, 59) : Color.GRAY);
        return l;
    }

    private void EstiloBoton(JButton b) {
        b.setBackground(AZUL_ACCENTO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void initWindow() {
        setTitle("Login");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LogginApp().setVisible(true));
    }
}