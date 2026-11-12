package br.com.lavamais.lavamais_api.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.CadastroUsuarioRequest;
import br.com.lavamais.lavamais_api.dto.UsuarioResponse;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String emailAdmin;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder,
                          @Value("${lavamais.admin.email:}") String emailAdmin) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailAdmin = emailAdmin;
    }

    /** O cadastro público só cria clientes; o e-mail do administrador nunca pode ser usado aqui. */
    public UsuarioResponse cadastrar(CadastroUsuarioRequest req) {
        if (!emailAdmin.isBlank() && emailAdmin.equalsIgnoreCase(req.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (repository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (req.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha muito longa");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email().trim());
        u.setTelefone(req.telefone());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        return UsuarioResponse.de(repository.save(u));
    }
}
