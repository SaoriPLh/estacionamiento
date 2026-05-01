package Estacionamiento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogRegistro extends JDialog {
    private JTextField txtCajon, txtPlaca;
    private JComboBox<String> cbTipo;

    public DialogRegistro(JFrame parent) {
        super(parent, "Registrar Entrada", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitulo = new JLabel("Nuevo Registro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblTitulo);
        content.add(Box.createRigidArea(new Dimension(0, 25)));

        content.add(crearLabel("Número de Cajón:"));
        txtCajon = crearTextField();
        content.add(txtCajon);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(crearLabel("Placa del Vehículo:"));
        txtPlaca = crearTextField();
        content.add(txtPlaca);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(crearLabel("Tipo de Cliente:"));
        cbTipo = new JComboBox<>(new String[]{"Normal", "VIP"});
        cbTipo.setMaximumSize(new Dimension(400, 40));
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTipo.setBackground(Color.WHITE);
        content.add(cbTipo);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnGuardar = new JButton("Confirmar Registro");
        btnGuardar.setMaximumSize(new Dimension(400, 50));
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.setBackground(new Color(13, 110, 253)); // Tu azul característico
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setContentAreaFilled(true);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
        btnGuardar.addActionListener(e -> {
            try {
                // Validar que el campo de cajón no esté vacío
                if(txtCajon.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingresa un número de cajón.");
                    return;
                }

                int num = Integer.parseInt(txtCajon.getText());
                String tipo = cbTipo.getSelectedItem().toString();
                String placa = txtPlaca.getText().trim();
                
                // Obtener hora actual
                String hora = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
                );

                // Validar placa
                if(placa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La placa no puede estar vacía.");
                    return;
                }

                // Validar rango de cajones
                if(num < 1 || num > 18) {
                    JOptionPane.showMessageDialog(this, "El número de cajón debe estar entre 1 y 18.");
                    return;
                }

                // Llamada segura
                MainDashboard ds = (MainDashboard) getOwner();
                ds.actualizarEstadoCajon(num, tipo, placa, hora);
                
                dispose(); // Cerrar ventana tras éxito
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El número de cajón debe ser un valor numérico entero.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado. Inténtalo de nuevo.");
                ex.printStackTrace();
            }
        });
        
        content.add(btnGuardar);
        add(content);
    }

    // Métodos auxiliares para mantener el estilo limpio
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(70, 70, 70));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(400, 40));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }
}