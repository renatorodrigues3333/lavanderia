package br.com.lavamais.lavamais_api.service;

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

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(CadastroUsuarioRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setTelefone(req.telefone());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        return UsuarioResponse.de(repository.save(u));
    }
}
