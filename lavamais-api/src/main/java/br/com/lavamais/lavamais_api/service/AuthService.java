package br.com.lavamais.lavamais_api.service;

import br.com.lavamais.lavamais_api.config.SegurancaConfig;
import br.com.lavamais.lavamais_api.dto.LoginRequest;
import br.com.lavamais.lavamais_api.dto.LoginResponse;
import br.com.lavamais.lavamais_api.dto.UsuarioLogadoResponse;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final TentativasLoginService tentativas;
    private final String emailAdmin;
    private final long expiracaoMinutos;
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder encoder,
                       JwtEncoder jwtEncoder,
                       TentativasLoginService tentativas,
                       @Value("${lavamais.admin.email:}") String emailAdmin,
                       @Value("${lavamais.jwt.expiracao-minutos:60}") long expiracaoMinutos) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.tentativas = tentativas;
        this.emailAdmin = emailAdmin;
        this.expiracaoMinutos = expiracaoMinutos;
        // usado para gastar o mesmo tempo quando o e-mail não existe
        this.hashFalso = encoder.encode("senha-falsa-para-igualar-o-tempo");
    }

    public LoginResponse login(LoginRequest req, String ip) {
        String email = req.email().trim();
        String chave = email.toLowerCase() + "|" + ip;
        tentativas.verificarBloqueio(chave);

        boolean tamanhoValido = req.senha().getBytes(StandardCharsets.UTF_8).length <= 72;
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        boolean autenticado = false;
        if (usuario == null) {
            if (tamanhoValido) {
                encoder.matches(req.senha(), hashFalso);
            }
        } else if (tamanhoValido) {
            autenticado = Boolean.TRUE.equals(usuario.getAtivo())
                    && encoder.matches(req.senha(), usuario.getSenhaHash());
        }

        if (!autenticado) {
            tentativas.registrarFalha(chave);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        tentativas.limpar(chave);
        return emitir(usuario);
    }

    public UsuarioLogadoResponse perfil(Integer usuarioId) {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
        return paraResposta(u);
    }

    private LoginResponse emitir(Usuario u) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(SegurancaConfig.EMISSOR)
                .issuedAt(agora)
                .expiresAt(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES))
                .subject(String.valueOf(u.getId()))
                .claim("email", u.getEmail())
                .claim("roles", List.of(ehAdmin(u.getEmail()) ? "ROLE_ADMIN" : "ROLE_CLIENTE"))
                .build();
        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new LoginResponse(token, "Bearer", expiracaoMinutos * 60, paraResposta(u));
    }

    private UsuarioLogadoResponse paraResposta(Usuario u) {
        return new UsuarioLogadoResponse(u.getId(), u.getNome(), u.getEmail(), ehAdmin(u.getEmail()));
    }

    private boolean ehAdmin(String email) {
        return !emailAdmin.isBlank() && emailAdmin.equalsIgnoreCase(email);
    }
}
