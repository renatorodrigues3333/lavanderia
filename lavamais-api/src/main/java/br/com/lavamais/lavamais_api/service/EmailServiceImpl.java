package br.com.lavamais.lavamais_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String emailRemetente;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
                            @Value("${spring.mail.username:noreply@lavamais.com}") String emailRemetente) {
        this.mailSenderProvider = mailSenderProvider;
        this.emailRemetente = emailRemetente;
    }

    @Override
    public void enviarCodigoRecuperacao(String paraEmail, String codigo) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender != null) {
            try {
                SimpleMailMessage mensagem = new SimpleMailMessage();
                mensagem.setFrom(emailRemetente);
                mensagem.setTo(paraEmail);
                mensagem.setSubject("LavaMais - Código de Recuperação de Senha");
                mensagem.setText("Olá,\n\nSeu código de recuperação de senha é: " + codigo +
                        "\n\nEste código é válido por 15 minutos. Caso não tenha solicitado, ignore este e-mail.");
                sender.send(mensagem);
                log.info("E-mail de recuperação de senha enviado com sucesso para {}", paraEmail);
            } catch (Exception e) {
                log.error("Falha ao enviar e-mail de recuperação para {}: {}", paraEmail, e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Falha ao enviar o e-mail de recuperação. Tente novamente mais tarde.", e);
            }
        } else {
            log.info("[DEV/SIMULAÇÃO] JavaMailSender não configurado. Código de recuperação para {}: {}", paraEmail, codigo);
        }
    }
}
