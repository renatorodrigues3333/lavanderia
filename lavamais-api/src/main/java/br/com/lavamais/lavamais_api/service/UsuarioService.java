package br.com.lavamais.lavamais_api.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.AtualizarPerfilRequest;
import br.com.lavamais.lavamais_api.dto.CadastroUsuarioRequest;
import br.com.lavamais.lavamais_api.dto.UsuarioResponse;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

@Service
@Transactional
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
        String emailNormalizado = req.email().trim().toLowerCase();
        if (!emailAdmin.isBlank() && emailAdmin.equalsIgnoreCase(emailNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (repository.existsByEmail(emailNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (req.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha muito longa");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setEmail(emailNormalizado);
        String telefone = (req.telefone() != null && !req.telefone().isBlank()) ? req.telefone().trim() : null;
        u.setTelefone(telefone);
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        return UsuarioResponse.de(repository.save(u));
    }

    /** Atualiza o perfil do usuário logado (nome, e-mail, telefone e/ou senha). */
    public UsuarioResponse atualizarPerfil(Integer usuarioId, AtualizarPerfilRequest req) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String novoEmail = req.email().trim().toLowerCase();
        boolean alterouEmail = !novoEmail.equalsIgnoreCase(usuario.getEmail());
        boolean alterouSenha = req.novaSenha() != null && !req.novaSenha().isBlank();

        if (alterouEmail || alterouSenha) {
            if (req.senhaAtual() == null || req.senhaAtual().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual é obrigatória para alterar e-mail ou senha");
            }
            if (!passwordEncoder.matches(req.senhaAtual(), usuario.getSenhaHash())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
            }
        }

        if (alterouEmail) {
            if (!emailAdmin.isBlank() && emailAdmin.equalsIgnoreCase(novoEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
            }
            if (repository.existsByEmail(novoEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
            }
            usuario.setEmail(novoEmail);
        }

        if (alterouSenha) {
            if (req.novaSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha muito longa");
            }
            if (req.novaSenha().length() < 6) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ter no mínimo 6 caracteres");
            }
            usuario.setSenhaHash(passwordEncoder.encode(req.novaSenha()));
        }

        usuario.setNome(req.nome().trim());
        String telefone = (req.telefone() != null && !req.telefone().isBlank()) ? req.telefone().trim() : null;
        usuario.setTelefone(telefone);

        return UsuarioResponse.de(repository.save(usuario));
    }
}
