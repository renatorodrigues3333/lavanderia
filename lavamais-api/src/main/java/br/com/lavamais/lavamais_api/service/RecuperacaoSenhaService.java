package br.com.lavamais.lavamais_api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.MensagemResponse;
import br.com.lavamais.lavamais_api.dto.RedefinirSenhaRequest;
import br.com.lavamais.lavamais_api.dto.SolicitarRecuperacaoRequest;
import br.com.lavamais.lavamais_api.dto.ValidarCodigoRequest;
import br.com.lavamais.lavamais_api.dto.ValidarCodigoResponse;
import br.com.lavamais.lavamais_api.entity.RecuperacaoSenha;
import br.com.lavamais.lavamais_api.entity.TipoOperacaoSenha;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.RecuperacaoSenhaRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

@Service
@Transactional
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TentativasValidacaoService tentativasValidacao;
    private final SecureRandom random = new SecureRandom();

    public RecuperacaoSenhaService(UsuarioRepository usuarioRepository,
                                   RecuperacaoSenhaRepository recuperacaoSenhaRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService,
                                   TentativasValidacaoService tentativasValidacao) {
        this.usuarioRepository = usuarioRepository;
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tentativasValidacao = tentativasValidacao;
    }

    /** Solicita o código de recuperação enviando um e-mail com 6 dígitos. Mensagem genérica por segurança. */
    public MensagemResponse solicitar(SolicitarRecuperacaoRequest req, String ipCliente) {
        String email = req.email().trim().toLowerCase();
        tentativasValidacao.verificarERegistrarSolicitacao(ipCliente, email);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null) {
            // Invalida solicitações anteriores ativas do mesmo usuário
            LocalDateTime agora = LocalDateTime.now();
            List<RecuperacaoSenha> ativas = recuperacaoSenhaRepository
                    .findByUsuarioIdAndConcluidoEmIsNullAndExpiraEmAfterOrderByCriadoEmDesc(usuario.getId(), agora);
            for (RecuperacaoSenha r : ativas) {
                r.setExpiraEm(agora);
                recuperacaoSenhaRepository.save(r);
            }

            // Gera código numérico aleatório de 6 dígitos
            String codigo = String.format("%06d", random.nextInt(1_000_000));

            RecuperacaoSenha rec = new RecuperacaoSenha();
            rec.setUsuarioId(usuario.getId());
            rec.setTipoOperacao(TipoOperacaoSenha.redefinir);
            rec.setCodigoHash(passwordEncoder.encode(codigo));
            rec.setExpiraEm(agora.plusMinutes(15));
            recuperacaoSenhaRepository.save(rec);

            emailService.enviarCodigoRecuperacao(usuario.getEmail(), codigo);
        }

        return new MensagemResponse("Se o e-mail estiver cadastrado, o código de recuperação foi enviado");
    }

    /** Valida o código de 6 dígitos enviado por e-mail e retorna um token temporário de redefinição. */
    public ValidarCodigoResponse validarCodigo(ValidarCodigoRequest req, String ipCliente) {
        String email = req.email().trim().toLowerCase();
        tentativasValidacao.verificarBloqueioValidacao(ipCliente, email);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            tentativasValidacao.registrarFalhaValidacao(ipCliente, email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado");
        }

        RecuperacaoSenha rec = recuperacaoSenhaRepository
                .findFirstByUsuarioIdOrderByCriadoEmDesc(usuario.getId())
                .orElse(null);

        LocalDateTime agora = LocalDateTime.now();
        if (rec == null
                || rec.getConcluidoEm() != null
                || rec.getUtilizadoEm() != null
                || rec.getExpiraEm().isBefore(agora)
                || !passwordEncoder.matches(req.codigo().trim(), rec.getCodigoHash())) {
            tentativasValidacao.registrarFalhaValidacao(ipCliente, email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado");
        }

        tentativasValidacao.limparValidacao(ipCliente, email);

        // Gera token temporário de redefinição com validade curta (10 minutos)
        String tokenRedefinicao = UUID.randomUUID().toString();
        String tokenHash = sha256Hex(tokenRedefinicao);

        rec.setUtilizadoEm(agora);
        rec.setTokenHash(tokenHash);
        rec.setExpiraEm(agora.plusMinutes(10));
        recuperacaoSenhaRepository.save(rec);

        return new ValidarCodigoResponse(tokenRedefinicao, "Código validado com sucesso. Use o token de redefinição fornecido.");
    }

    /** Redefine a senha utilizando o token temporário gerado na validação do código. */
    public MensagemResponse redefinirSenha(RedefinirSenhaRequest req, String ipCliente) {
        tentativasValidacao.verificarBloqueioRedefinicao(ipCliente);

        if (req.novaSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha muito longa");
        }
        if (req.novaSenha().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ter no mínimo 6 caracteres");
        }

        LocalDateTime agora = LocalDateTime.now();
        String hashTokenRecebido = sha256Hex(req.tokenRedefinicao().trim());

        RecuperacaoSenha recEncontrado = recuperacaoSenhaRepository
                .findByTokenHashAndConcluidoEmIsNullAndExpiraEmAfter(hashTokenRecebido, agora)
                .orElse(null);

        if (recEncontrado == null) {
            tentativasValidacao.registrarFalhaRedefinicao(ipCliente);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de redefinição inválido ou expirado");
        }

        Usuario usuario = usuarioRepository.findById(recEncontrado.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuario.setSenhaHash(passwordEncoder.encode(req.novaSenha()));
        usuarioRepository.save(usuario);

        recEncontrado.setConcluidoEm(agora);
        recuperacaoSenhaRepository.save(recEncontrado);

        return new MensagemResponse("Senha redefinida com sucesso");
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
