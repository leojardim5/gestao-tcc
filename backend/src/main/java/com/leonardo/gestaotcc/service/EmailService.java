package com.leonardo.gestaotcc.service;

import com.leonardo.gestaotcc.dto.EmailNotificationData;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.TipoNotificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, @Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Gestão TCC}")
    private String appName;

    public void sendNotificationEmail(Usuario usuario, TipoNotificacao tipo, String mensagem) {
        sendNotificationEmail(usuario, tipo, mensagem, null);
    }

    public void sendNotificationEmail(Usuario usuario, TipoNotificacao tipo, String mensagem, EmailNotificationData dadosExtras) {
        log.info("Tentando enviar email para: {}", usuario != null ? usuario.getEmail() : "null");
        
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            log.warn("Não foi possível enviar email: usuário ou email inválido");
            return;
        }

        log.info("Configurando email - From: {}, To: {}, Tipo: {}", fromEmail, usuario.getEmail(), tipo);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(usuario.getEmail());
            helper.setSubject(getEmailSubject(tipo, mensagem, dadosExtras));

            String htmlContent = buildEmailContent(usuario, tipo, mensagem, dadosExtras);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de notificação enviado com sucesso para: {}", usuario.getEmail());
        } catch (MessagingException e) {
            log.error("Erro ao enviar email de notificação para {}: {}", usuario.getEmail(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar email: {}", e.getMessage(), e);
        }
    }

    private String getEmailSubject(TipoNotificacao tipo, String mensagem, EmailNotificationData dadosExtras) {
        String prefixo = switch (tipo) {
            case CONVITE_ORIENTACAO -> "Convite de Orientação";
            case REUNIAO -> "Reunião Agendada";
            case PRAZO -> "Prazo Próximo";
            case COMENTARIO -> "Novo Comentário";
            case SISTEMA -> "Notificação do Sistema";
        };
        
        if (dadosExtras != null && dadosExtras.getTituloTcc() != null) {
            return String.format("[%s] %s - %s", appName, prefixo, dadosExtras.getTituloTcc());
        }
        
        return String.format("[%s] %s", appName, prefixo);
    }

    private String buildEmailContent(Usuario usuario, TipoNotificacao tipo, String mensagem, EmailNotificationData dadosExtras) {
        Context context = new Context(new Locale("pt", "BR"));
        context.setVariable("usuarioNome", usuario.getNome());
        context.setVariable("mensagem", mensagem);
        context.setVariable("tipo", tipo);
        context.setVariable("appName", appName);
        context.setVariable("tipoLabel", getTipoLabel(tipo));
        context.setVariable("dados", dadosExtras);
        context.setVariable("temDadosExtras", dadosExtras != null);
        
        // Formatação de datas
        if (dadosExtras != null && dadosExtras.getDataHora() != null) {
            context.setVariable("dataHoraFormatada", 
                dadosExtras.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")));
        }

        // Escolher template baseado no tipo
        String templateName = switch (tipo) {
            case CONVITE_ORIENTACAO -> "email/convite-orientacao";
            case REUNIAO -> "email/reuniao";
            case PRAZO -> "email/prazo";
            case COMENTARIO -> "email/comentario";
            case SISTEMA -> "email/notificacao";
        };

        return templateEngine.process(templateName, context);
    }

    private String getTipoLabel(TipoNotificacao tipo) {
        return switch (tipo) {
            case CONVITE_ORIENTACAO -> "Convite de Orientação";
            case REUNIAO -> "Reunião";
            case PRAZO -> "Prazo";
            case COMENTARIO -> "Comentário";
            case SISTEMA -> "Sistema";
        };
    }
}

