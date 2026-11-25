package br.com.lavamais.lavamais_api.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.AtualizarPerfilRequest;
import br.com.lavamais.lavamais_api.dto.CadastroUsuarioRequest;
import br.com.lavamais.lavamais_api.dto.UsuarioResponse;
import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.NotificacaoRepository;
import br.com.lavamais.lavamais_api.repository.RecuperacaoSenhaRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;
import br.com.lavamais.lavamais_api.repository.UtilizacaoRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String emailAdmin;
    private final NotificacaoRepository notificacaoRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final UtilizacaoRepository utilizacaoRepository;
    private final AuditoriaService auditoria;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder,
                          @Value("${lavamais.admin.email:}") String emailAdmin,
                          NotificacaoRepository notificacaoRepository,
                          RecuperacaoSenhaRepository recuperacaoSenhaRepository,
                          UtilizacaoRepository utilizacaoRepository,
                          AuditoriaService auditoria) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailAdmin = emailAdmin;
        this.notificacaoRepository = notificacaoRepository;
        this.recuperacaoSenhaRepository = recuperacaoSenhaRepository;
        this.utilizacaoRepository = utilizacaoRepository;
        this.auditoria = auditoria;
    }

    /**
     * Remove a conta de um cliente (pedido de exclusão de dados). Apaga o cadastro, as notificações
     * e os códigos de recuperação de senha. Pedidos, pagamentos e chamados ficam como histórico,
     * já sem nome, e-mail ou telefone ligados a eles.
     */
    public void remover(Integer id) {
        SessaoUsuario.exigirAdmin();
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (!emailAdmin.isBlank() && emailAdmin.equalsIgnoreCase(u.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A conta do administrador não pode ser removida");
        }
        if (utilizacaoRepository.existsByUsuarioIdAndEstado(id, EstadoUtilizacao.em_andamento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O usuário tem um ciclo em andamento. Aguarde o término para remover a conta");
        }

        notificacaoRepository.deleteByUsuarioId(id);
        recuperacaoSenhaRepository.deleteByUsuarioId(id);
        repository.delete(u);
        auditoria.registrar(SessaoUsuario.id(), "remover_usuario", "usuario", id, null);
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
