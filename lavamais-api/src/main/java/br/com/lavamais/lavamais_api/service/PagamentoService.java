package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.PagamentoRequest;
import br.com.lavamais.lavamais_api.dto.PagamentoResponse;
import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Pagamento;
import br.com.lavamais.lavamais_api.entity.Utilizacao;
import br.com.lavamais.lavamais_api.repository.PagamentoRepository;
import br.com.lavamais.lavamais_api.repository.UtilizacaoRepository;

@Service
@Transactional
public class PagamentoService {

    private final PagamentoRepository repository;
    private final UtilizacaoRepository utilizacaoRepository;
    private final AuditoriaService auditoria;
    private final boolean simulacaoHabilitada;

    public PagamentoService(PagamentoRepository repository,
                            UtilizacaoRepository utilizacaoRepository,
                            AuditoriaService auditoria,
                            @Value("${lavamais.pagamento.simulacao-habilitada:false}") boolean simulacaoHabilitada) {
        this.repository = repository;
        this.utilizacaoRepository = utilizacaoRepository;
        this.auditoria = auditoria;
        this.simulacaoHabilitada = simulacaoHabilitada;
    }

    /** O valor vem sempre da utilização, nunca do app. */
    public PagamentoResponse criar(PagamentoRequest req) {
        Utilizacao u = obterUtilizacao(req.utilizacaoId());
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        if (u.getEstado() != EstadoUtilizacao.pendente) {
            throw erro(HttpStatus.CONFLICT, "A utilização não está pendente de pagamento");
        }
        if (repository.existsByUtilizacaoIdAndEstado(u.getId(), EstadoPagamento.aprovado)) {
            throw erro(HttpStatus.CONFLICT, "Utilização já paga");
        }

        Pagamento p = new Pagamento();
        p.setUtilizacaoId(u.getId());
        p.setValor(u.getValorTotal());
        p.setMetodo(req.metodo());
        p.setProvedor(req.provedor());
        p.setReferenciaExterna(req.referenciaExterna());
        Pagamento salvo = repository.save(p);

        auditoria.registrar(SessaoUsuario.id(), "criar_pagamento", "pagamento", salvo.getId(), null);
        return PagamentoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscar(Integer id) {
        Pagamento p = obter(id);
        SessaoUsuario.exigirDonoOuAdmin(obterUtilizacao(p.getUtilizacaoId()).getUsuarioId());
        return PagamentoResponse.de(p);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorUtilizacao(Integer utilizacaoId) {
        Utilizacao u = obterUtilizacao(utilizacaoId);
        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());
        return repository.findByUtilizacaoId(utilizacaoId)
                .stream().map(PagamentoResponse::de).toList();
    }

    /** Só o administrador (até existir a integração com o provedor de pagamento). */
    public PagamentoResponse alterarEstado(Integer id, EstadoPagamento novo) {
        SessaoUsuario.exigirAdmin();
        Pagamento p = obter(id);
        p.setEstado(novo);
        if (novo == EstadoPagamento.aprovado && p.getPagoEm() == null) {
            p.setPagoEm(LocalDateTime.now());
        }
        Pagamento salvo = repository.save(p);
        auditoria.registrar(SessaoUsuario.id(), "pagamento_" + novo.name(), "pagamento", salvo.getId(), null);
        return PagamentoResponse.de(salvo);
    }

    /** Simula a aprovação do pagamento em ambiente de desenvolvimento. */
    public PagamentoResponse simularAprovacao(Integer id) {
        if (!simulacaoHabilitada) {
            throw erro(HttpStatus.FORBIDDEN, "Aprovação simulada de pagamentos está desabilitada neste ambiente");
        }

        // 1. Localiza o pagamento sem bloqueio para obter o utilizacaoId
        Pagamento pInicial = repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

        // 2. ORDEM DE BLOQUEIO: Bloqueia primeiro a Utilização (Pai)
        Utilizacao u = utilizacaoRepository.findByIdComBloqueio(pInicial.getUtilizacaoId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Utilização não encontrada"));

        // 3. Bloqueia em seguida o Pagamento (Filho)
        Pagamento p = repository.findByIdComBloqueio(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

        SessaoUsuario.exigirDonoOuAdmin(u.getUsuarioId());

        if (u.getEstado() != EstadoUtilizacao.pendente) {
            throw erro(HttpStatus.CONFLICT, "A utilização associada a este pagamento não está pendente");
        }

        if (p.getEstado() == EstadoPagamento.aprovado) {
            throw erro(HttpStatus.CONFLICT, "Este pagamento já foi aprovado");
        }

        if (p.getEstado() != EstadoPagamento.pendente) {
            throw erro(HttpStatus.CONFLICT, "Apenas pagamentos pendentes podem ser aprovados");
        }

        p.setEstado(EstadoPagamento.aprovado);
        p.setPagoEm(LocalDateTime.now());
        Pagamento salvo = repository.save(p);

        auditoria.registrar(SessaoUsuario.id(), "simular_aprovacao_pagamento", "pagamento", salvo.getId(), null);
        return PagamentoResponse.de(salvo);
    }

    private Pagamento obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));
    }

    private Utilizacao obterUtilizacao(Integer id) {
        return utilizacaoRepository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Utilização não encontrada"));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
