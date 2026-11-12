package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.NotificacaoRequest;
import br.com.lavamais.lavamais_api.dto.NotificacaoResponse;
import br.com.lavamais.lavamais_api.entity.Notificacao;
import br.com.lavamais.lavamais_api.repository.NotificacaoRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;
import br.com.lavamais.lavamais_api.repository.UtilizacaoRepository;

@Service
@Transactional
public class NotificacaoService {

    private final NotificacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final UtilizacaoRepository utilizacaoRepository;

    public NotificacaoService(NotificacaoRepository repository,
                              UsuarioRepository usuarioRepository,
                              UtilizacaoRepository utilizacaoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.utilizacaoRepository = utilizacaoRepository;
    }

    /** Só o administrador cria notificações. */
    public NotificacaoResponse criar(NotificacaoRequest req) {
        SessaoUsuario.exigirAdmin();
        if (req.usuarioId() != null && !usuarioRepository.existsById(req.usuarioId())) {
            throw erro(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        if (req.utilizacaoId() != null && !utilizacaoRepository.existsById(req.utilizacaoId())) {
            throw erro(HttpStatus.NOT_FOUND, "Utilização não encontrada");
        }
        Notificacao n = new Notificacao();
        n.setUsuarioId(req.usuarioId());
        n.setUtilizacaoId(req.utilizacaoId());
        n.setTipo(req.tipo());
        n.setTitulo(req.titulo());
        n.setMensagem(req.mensagem());
        return NotificacaoResponse.de(repository.save(n));
    }

    /** Cliente: as suas e as gerais. Administrador: as do usuário informado, ou todas. */
    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listar(Integer usuarioId) {
        List<Notificacao> lista;
        if (SessaoUsuario.admin()) {
            lista = usuarioId == null
                    ? repository.findAllByOrderByCriadoEmDesc()
                    : repository.findByUsuarioIdOrUsuarioIdIsNullOrderByCriadoEmDesc(usuarioId);
        } else {
            lista = repository.findByUsuarioIdOrUsuarioIdIsNullOrderByCriadoEmDesc(SessaoUsuario.id());
        }
        return lista.stream().map(NotificacaoResponse::de).toList();
    }

    public NotificacaoResponse marcarLida(Integer id) {
        Notificacao n = repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        if (n.getUsuarioId() == null) {
            // notificação geral é compartilhada: só o administrador altera
            SessaoUsuario.exigirAdmin();
        } else {
            SessaoUsuario.exigirDonoOuAdmin(n.getUsuarioId());
        }
        if (n.getLidoEm() == null) {
            n.setLidoEm(LocalDateTime.now());
        }
        return NotificacaoResponse.de(repository.save(n));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
