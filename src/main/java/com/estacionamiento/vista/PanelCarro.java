package Estacionamiento;

import javax.swing.*;
import java.awt.*;

public class PanelCarro extends JPanel {

    private int numeroEspacio;
    private boolean ocupado;
    private ImageIcon iconoCarro;

    public PanelCarro(int numeroEspacio, boolean ocupado, ImageIcon iconoCarro) {
        this.numeroEspacio = numeroEspacio;
        this.ocupado = ocupado;
        this.iconoCarro = iconoCarro;

        setLayout(null);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        if (ocupado) {
            setBackground(new Color(255, 234, 239));
        } else {
            setBackground(Color.WHITE);
        }

        JLabel lblNumero = new JLabel(String.valueOf(numeroEspacio));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblNumero.setForeground(Color.GRAY);
        lblNumero.setBounds(5, 5, 20, 15);
        add(lblNumero);

        JLabel lblEstado = new JLabel(ocupado ? "Ocupado" : "Libre", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (ocupado) {
            lblEstado.setForeground(new Color(255, 90, 130)); // Rojo suave
        } else {
            lblEstado.setForeground(new Color(40, 190, 130)); // Verde suave
        }
        lblEstado.setBounds(0, 100, 155, 20); 
        add(lblEstado);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (ocupado && iconoCarro != null) {
            int x = (getWidth() - iconoCarro.getIconWidth()) / 2;
            int y = (getHeight() - iconoCarro.getIconHeight()) / 2 - 10; // Subir un poco
            g.drawImage(iconoCarro.getImage(), x, y, this);
        }
    }
}