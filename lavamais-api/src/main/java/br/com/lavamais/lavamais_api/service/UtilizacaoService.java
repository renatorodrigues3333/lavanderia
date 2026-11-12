package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.UtilizacaoRequest;
import br.com.lavamais.lavamais_api.dto.UtilizacaoResponse;
import br.com.lavamais.lavamais_api.entity.EstadoMaquina;
import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Maquina;
import br.com.lavamais.lavamais_api.entity.Servico;
import br.com.lavamais.lavamais_api.entity.Utilizacao;
import br.com.lavamais.lavamais_api.repository.MaquinaRepository;
import br.com.lavamais.lavamais_api.repository.PagamentoRepository;
import br.com.lavamais.lavamais_api.repository.ServicoRepository;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;
import br.com.lavamais.lavamais_api.repository.UtilizacaoRepository;

/**
 * Fluxo: criar (pendente) -> pagamento aprovado -> iniciar (em_andamento) -> concluir (concluida).
 * Uma utilização pendente também pode ser cancelada.
 * O cliente só enxerga e mexe nas próprias utilizações; o administrador, em todas.
 */
@Service
@Transactional
public class UtilizacaoService {

    private final UtilizacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final MaquinaRepository maquinaRepository;
    private final ServicoRepository servicoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final HistoricoMaquinaService historico;
    private final AuditoriaService auditoria;

    public UtilizacaoService(UtilizacaoRepository repository,
                             UsuarioRepository usuarioRepository,
                             MaquinaRepository maquinaRepository,
                             ServicoRepository servicoRepository,
                             PagamentoRepository pagamentoRepository,
                             HistoricoMaquinaService historico,
                             AuditoriaService auditoria) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.maquinaRepository = maquinaRepository;
        this.servicoRepository = servicoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.historico = historico;
        this.auditoria = auditoria;
    }

    public UtilizacaoResponse criar(UtilizacaoRequest req) {
        Integer usuarioId = SessaoUsuario.admin() && req.usuarioId() != null
                ? req.usuarioId()
                : SessaoUsuario.id();
        if (!usuarioRepository.existsById(usuarioId)) {
            throw erro(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        Maquina maquina = maquinaRepository.findById(req.maquinaId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
        if (!maquina.getAtiva() || maquina.getEstado() != EstadoMaquina.livre) {
            throw erro(HttpStatus.CONFLICT, "Máquina indisponível");
        }
        Servico servico = servicoRepository.findById(req.servicoId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
        if (!servico.getAtivo()) {
            throw erro(HttpStatus.CONFLICT, "Serviço indisponível");
        }

        Utilizacao u = new Utilizacao();
        u.setUsuarioId(usuarioId);
        u.setMaquinaId(req.maquinaId());
        u.setServicoId(req.servicoId());
        u.setInicioPrevisto(req.inicioPrevisto());
        u.setValorTotal(servico.getPreco());
        Utilizacao salva = repository.save(u);

        auditoria.registrar(SessaoUsuario.id(), "criar_utilizacao", "utilizacao", salva.getId(), null);
        return UtilizacaoResponse.de(salva);
    }

    @Transactional(readOnly = true)
    public UtilizacaoResponse buscar(Integer id) {
        Utilizacao u = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        return UtilizacaoResponse.de(u);
    }

    /** Cliente: sempre as próprias. Administrador: as do usuário informado, ou todas. */
    @Transactional(readOnly = true)
    public List<UtilizacaoResponse> listar(Integer usuarioId) {
        List<Utilizacao> lista;
        if (SessaoUsuario.admin()) {
            lista = usuarioId == null
                    ? repository.findAllByOrderByCriadoEmDesc()
                    : repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
        } else {
            lista = repository.findByUsuarioIdOrderByCriadoEmDesc(SessaoUsuario.id());
        }
        return lista.stream().map(UtilizacaoResponse::de).toList();
    }

    public UtilizacaoResponse iniciar(Integer id) {
        Utilizacao u = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        if (u.getEstado() != EstadoUtilizacao.pendente) {
            throw erro(HttpStatus.CONFLICT, "Só é possível iniciar uma utilização pendente");
        }
        if (!pagamentoRepository.existsByUtilizacaoIdAndEstado(id, EstadoPagamento.aprovado)) {
            throw erro(HttpStatus.CONFLICT, "Pagamento não aprovado");
        }
        Maquina m = maquinaRepository.findById(u.getMaquinaId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
        if (!m.getAtiva() || m.getEstado() != EstadoMaquina.livre) {
            throw erro(HttpStatus.CONFLICT, "Máquina indisponível");
        }

        String anterior = m.getEstado().name();
        m.setEstado(EstadoMaquina.em_uso);
        maquinaRepository.save(m);

        u.setEstado(EstadoUtilizacao.em_andamento);
        u.setInicioReal(LocalDateTime.now());
        Utilizacao salva = repository.save(u);

        historico.registrar(m.getId(), u.getUsuarioId(), u.getId(), "inicio_utilizacao",
                anterior, EstadoMaquina.em_uso.name(), null);
        auditoria.registrar(SessaoUsuario.id(), "iniciar_utilizacao", "utilizacao", u.getId(), null);
        return UtilizacaoResponse.de(salva);
    }

    public UtilizacaoResponse concluir(Integer id) {
        Utilizacao u = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        if (u.getEstado() != EstadoUtilizacao.em_andamento) {
            throw erro(HttpStatus.CONFLICT, "Só é possível concluir uma utilização em andamento");
        }
        Maquina m = maquinaRepository.findById(u.getMaquinaId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Máquina não encontrada"));

        String anterior = m.getEstado().name();
        m.setEstado(EstadoMaquina.livre);
        maquinaRepository.save(m);

        u.setEstado(EstadoUtilizacao.concluida);
        u.setFimReal(LocalDateTime.now());
        Utilizacao salva = repository.save(u);

        historico.registrar(m.getId(), u.getUsuarioId(), u.getId(), "fim_utilizacao",
                anterior, EstadoMaquina.livre.name(), null);
        auditoria.registrar(SessaoUsuario.id(), "concluir_utilizacao", "utilizacao", u.getId(), null);
        return UtilizacaoResponse.de(salva);
    }

    public UtilizacaoResponse cancelar(Integer id) {
        Utilizacao u = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        if (u.getEstado() != EstadoUtilizacao.pendente) {
            throw erro(HttpStatus.CONFLICT, "Só é possível cancelar uma utilização pendente");
        }
        u.setEstado(EstadoUtilizacao.cancelada);
        Utilizacao salva = repository.save(u);
        auditoria.registrar(SessaoUsuario.id(), "cancelar_utilizacao", "utilizacao", u.getId(), null);
        return UtilizacaoResponse.de(salva);
    }

    private Utilizacao obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Utilização não encontrada"));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
