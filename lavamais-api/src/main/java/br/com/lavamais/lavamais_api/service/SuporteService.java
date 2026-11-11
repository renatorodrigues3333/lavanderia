package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.MensagemSuporteRequest;
import br.com.lavamais.lavamais_api.dto.MensagemSuporteResponse;
import br.com.lavamais.lavamais_api.dto.SuporteRequest;
import br.com.lavamais.lavamais_api.dto.SuporteResponse;
import br.com.lavamais.lavamais_api.entity.EstadoSuporte;
import br.com.lavamais.lavamais_api.entity.MensagemSuporte;
import br.com.lavamais.lavamais_api.entity.Suporte;
import br.com.lavamais.lavamais_api.repository.MensagemSuporteRepository;
import br.com.lavamais.lavamais_api.repository.SuporteRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

@Service
@Transactional
public class SuporteService {

    private final SuporteRepository repository;
    private final MensagemSuporteRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;

    public SuporteService(SuporteRepository repository,
                          MensagemSuporteRepository mensagemRepository,
                          UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.mensagemRepository = mensagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SuporteResponse abrir(SuporteRequest req) {
        if (!usuarioRepository.existsById(req.usuarioId())) {
            throw erro(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        Suporte s = new Suporte();
        s.setUsuarioId(req.usuarioId());
        s.setTema(req.tema());
        s.setDescricao(req.descricao());
        return SuporteResponse.de(repository.save(s));
    }

    @Transactional(readOnly = true)
    public SuporteResponse buscar(Integer id) {
        return SuporteResponse.de(obter(id));
    }

    /** Sem usuarioId lista todos os chamados (uso do administrador). */
    @Transactional(readOnly = true)
    public List<SuporteResponse> listar(Integer usuarioId) {
        List<Suporte> lista = usuarioId == null
                ? repository.findAllByOrderByCriadoEmDesc()
                : repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
        return lista.stream().map(SuporteResponse::de).toList();
    }

    public SuporteResponse alterarEstado(Integer id, EstadoSuporte novo) {
        Suporte s = obter(id);
        s.setEstado(novo);
        if (novo == EstadoSuporte.resolvido && s.getResolvidoEm() == null) {
            s.setResolvidoEm(LocalDateTime.now());
        }
        return SuporteResponse.de(repository.save(s));
    }

    public MensagemSuporteResponse enviarMensagem(Integer suporteId, MensagemSuporteRequest req) {
        Suporte s = obter(suporteId);
        if (s.getEstado() == EstadoSuporte.resolvido || s.getEstado() == EstadoSuporte.cancelado) {
            throw erro(HttpStatus.CONFLICT, "Este chamado está encerrado");
        }
        if (!usuarioRepository.existsById(req.remetenteId())) {
            throw erro(HttpStatus.NOT_FOUND, "Remetente não encontrado");
        }
        MensagemSuporte m = new MensagemSuporte();
        m.setSuporteId(suporteId);
        m.setRemetenteId(req.remetenteId());
        m.setConteudo(req.conteudo());
        return MensagemSuporteResponse.de(mensagemRepository.save(m));
    }

    @Transactional(readOnly = true)
    public List<MensagemSuporteResponse> listarMensagens(Integer suporteId) {
        obter(suporteId);
        return mensagemRepository.findBySuporteIdOrderByEnviadaEmAsc(suporteId)
                .stream().map(MensagemSuporteResponse::de).toList();
    }

    public MensagemSuporteResponse marcarMensagemLida(Integer mensagemId) {
        MensagemSuporte m = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Mensagem não encontrada"));
        if (m.getLidaEm() == null) {
            m.setLidaEm(LocalDateTime.now());
        }
        return MensagemSuporteResponse.de(mensagemRepository.save(m));
    }

    private Suporte obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Chamado não encontrado"));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
