// service/EmailService.java
package com.joaquin.pistasdeportivas.service;

import com.joaquin.pistasdeportivas.entity.Reserva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarConfirmacion(Reserva reserva) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(reserva.getUsuario().getEmail());
            msg.setSubject("✅ Confirmación de reserva - Pistas Deportivas");
            msg.setText(String.format("""
                    Hola %s,

                    Tu reserva ha sido confirmada:

                    Pista     : %s (%s)
                    Fecha     : %s
                    Horario   : %s — %s
                    Precio    : %.2f €

                    ¡Hasta pronto!
                    Pistas Deportivas
                    """,
                    reserva.getUsuario().getNombre(),
                    reserva.getPista().getNombre(),
                    reserva.getPista().getTipo(),
                    reserva.getFecha(),
                    reserva.getHoraInicio(),
                    reserva.getHoraFin(),
                    reserva.getPrecio()));
            mailSender.send(msg);
            log.info("Email de confirmación enviado a {}", reserva.getUsuario().getEmail());
        } catch (Exception e) {
            // El fallo de email no debe interrumpir la reserva
            log.error("Error al enviar email: {}", e.getMessage());
        }
    }

    public void enviarCancelacion(Reserva reserva, BigDecimal reembolso) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(reserva.getUsuario().getEmail());
            msg.setSubject("❌ Cancelación de reserva - Pistas Deportivas");
            msg.setText(String.format("""
                    Hola %s,

                    Tu reserva ha sido cancelada:

                    Pista     : %s
                    Fecha     : %s
                    Horario   : %s — %s
                    Precio    : %.2f €
                    Reembolso : %.2f €

                    Pistas Deportivas
                    """,
                    reserva.getUsuario().getNombre(),
                    reserva.getPista().getNombre(),
                    reserva.getFecha(),
                    reserva.getHoraInicio(),
                    reserva.getHoraFin(),
                    reserva.getPrecio(),
                    reembolso));
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Error al enviar email de cancelación: {}", e.getMessage());
        }
    }
}
