package com.estacionamiento.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static Session crearSesion() {
        String host  = ConfiguracionLocal.get("email.host");
        String puerto = ConfiguracionLocal.get("email.puerto");
        String usuario = ConfiguracionLocal.get("email.usuario");
        String password = ConfiguracionLocal.get("email.password");

        System.out.println("HOST: " + host);
System.out.println("USER: " + usuario);
System.out.println("PORT: " + puerto);

        if (host == null || usuario == null || password == null) return null;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto != null ? puerto : "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });
    }

    public static void enviar(String destinatario, String asunto, String cuerpo) {
        if (destinatario == null || destinatario.isBlank()) return;
        Session session = crearSesion();
        if (session == null) {
            System.err.println("[Email] Sin configuración SMTP. Configura email.host/usuario/password en ajustes.");
            return;
        }
        String remitente = ConfiguracionLocal.get("email.usuario");
        new Thread(() -> {
            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(remitente));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                msg.setSubject(asunto);
                msg.setText(cuerpo);
                Transport.send(msg);
                System.out.println("[Email] Enviado a " + destinatario);
            } catch (MessagingException e) {
                System.err.println("[Email] Error enviando a " + destinatario + ": " + e.getMessage());
            }
        }, "email-sender").start();
    }

    public static void notificarNuevaPension(String correo, String nombreCliente, String placa,
                                              String fechaInicio, String fechaFin, double monto, Double descuento) {
        String asunto = "Confirmacion de tu plan de pension";
        String cuerpo = "Hola " + nombreCliente + ",\n\n"
                + "Tu plan de pension ha sido registrado exitosamente.\n\n"
                + "  Vehiculo : " + placa + "\n"
                + "  Inicio   : " + fechaInicio + "\n"
                + "  Vigente  : " + fechaFin + "\n"
                + "  Descuento  : " + descuento  + "\n"
                + "  Pagado   : $" + String.format("%.2f", monto) + "\n\n"
                + "Conserva este comprobante. Te avisaremos cuando tu plan este proximo a vencer.\n\n"
                + "Gracias por preferirnos.";
        enviar(correo, asunto, cuerpo);
    }

    public static void notificarVencimientoCliente(String correo, String nombreCliente, String placa, String fechaFin) {
        String asunto = "Tu plan de pensión vence pronto";
        String cuerpo = "Hola " + nombreCliente + ",\n\n"
                + "Tu plan de pensión para el vehículo con placa " + placa + " vence el " + fechaFin + ".\n"
                + "Acércate antes de esa fecha para renovarlo y conservar tu espacio.\n\n"
                + "¡Gracias por preferirnos!";
        enviar(correo, asunto, cuerpo);
    }

}
