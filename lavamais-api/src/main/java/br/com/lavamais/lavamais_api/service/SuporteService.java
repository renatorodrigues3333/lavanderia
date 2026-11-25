package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.MensagemSuporteRequest;
import br.com.lavamais.lavamais_api.dto.MensagemSuporteResponse;
import br.com.lavamais.lavamais_api.dto.SuporteRequest;
import br.com.lavamais.lavamais_api.dto.SuporteResponse;
import br.com.lavamais.lavamais_api.entity.EstadoSuporte;
import br.com.lavamais.lavamais_api.entity.MensagemSuporte;
import br.com.lavamais.lavamais_api.entity.Suporte;
import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.MensagemSuporteRepository;
import br.com.lavamais.lavamais_api.repository.SuporteRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

@Service
@Transactional
public class SuporteService {

    private final SuporteRepository repository;
    private final MensagemSuporteRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;

    public SuporteService(SuporteRepository repository, MensagemSuporteRepository mensagemRepository,
                          UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.mensagemRepository = mensagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** O chamado sempre pertence a quem está logado. */
    public SuporteResponse abrir(SuporteRequest req) {
        Suporte s = new Suporte();
        s.setUsuarioId(SessaoUsuario.id());
        s.setTema(req.tema());
        s.setDescricao(req.descricao());
        return paraResposta(repository.save(s));
    }

    @Transactional(readOnly = true)
    public SuporteResponse buscar(Integer id) {
        Suporte s = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(s.getUsuarioId());
        return paraResposta(s);
    }

    /** Cliente: só os próprios. Administrador: os do usuário informado, ou todos. */
    @Transactional(readOnly = true)
    public List<SuporteResponse> listar(Integer usuarioId) {
        List<Suporte> lista;
        if (SessaoUsuario.admin()) {
            lista = usuarioId == null
                    ? repository.findAllByOrderByCriadoEmDesc()
                    : repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
        } else {
            lista = repository.findByUsuarioIdOrderByCriadoEmDesc(SessaoUsuario.id());
        }
        return paraRespostas(lista);
    }

    public SuporteResponse alterarEstado(Integer id, EstadoSuporte novo) {
        SessaoUsuario.exigirAdmin();
        Suporte s = obter(id);
        s.setEstado(novo);
        if (novo == EstadoSuporte.resolvido && s.getResolvidoEm() == null) {
            s.setResolvidoEm(LocalDateTime.now());
        }
        return paraResposta(repository.save(s));
    }

    public MensagemSuporteResponse enviarMensagem(Integer suporteId, MensagemSuporteRequest req) {
        Suporte s = obter(suporteId);
        SessaoUsuario.exigirDonoOuAdmin(s.getUsuarioId());
        if (s.getEstado() == EstadoSuporte.resolvido || s.getEstado() == EstadoSuporte.cancelado) {
            throw erro(HttpStatus.CONFLICT, "Este chamado está encerrado");
        }
        MensagemSuporte m = new MensagemSuporte();
        m.setSuporteId(suporteId);
        m.setRemetenteId(SessaoUsuario.id());
        m.setConteudo(req.conteudo());
        return MensagemSuporteResponse.de(mensagemRepository.save(m));
    }

    @Transactional(readOnly = true)
    public List<MensagemSuporteResponse> listarMensagens(Integer suporteId) {
        Suporte s = obter(suporteId);
        SessaoUsuario.exigirDonoOuAdmin(s.getUsuarioId());
        return mensagemRepository.findBySuporteIdOrderByEnviadaEmAsc(suporteId)
                .stream().map(MensagemSuporteResponse::de).toList();
    }

    public MensagemSuporteResponse marcarMensagemLida(Integer mensagemId) {
        MensagemSuporte m = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Mensagem não encontrada"));
        Suporte s = obter(m.getSuporteId());
        SessaoUsuario.exigirDonoOuAdmin(s.getUsuarioId());
        if (m.getLidaEm() == null) {
            m.setLidaEm(LocalDateTime.now());
        }
        return MensagemSuporteResponse.de(mensagemRepository.save(m));
    }

    /** Junta o nome e o e-mail de quem abriu o chamado, para o administrador saber quem é. */
    private SuporteResponse paraResposta(Suporte s) {
        return SuporteResponse.de(s, usuarioRepository.findById(s.getUsuarioId()).orElse(null));
    }

    private List<SuporteResponse> paraRespostas(List<Suporte> lista) {
        Map<Integer, Usuario> usuarios = usuarioRepository
                .findAllById(lista.stream().map(Suporte::getUsuarioId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Usuario::getId, u -> u));
        return lista.stream().map(s -> SuporteResponse.de(s, usuarios.get(s.getUsuarioId()))).toList();
    }

    private Suporte obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Chamado não encontrado"));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
