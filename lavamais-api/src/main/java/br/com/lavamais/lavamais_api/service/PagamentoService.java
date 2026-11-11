package br.com.lavamais.lavamais_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public PagamentoService(PagamentoRepository repository,
                            UtilizacaoRepository utilizacaoRepository,
                            AuditoriaService auditoria) {
        this.repository = repository;
        this.utilizacaoRepository = utilizacaoRepository;
        this.auditoria = auditoria;
    }

    /** O valor vem sempre da utilização, nunca do app. */
    public PagamentoResponse criar(PagamentoRequest req) {
        Utilizacao u = utilizacaoRepository.findById(req.utilizacaoId())
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Utilização não encontrada"));
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

        auditoria.registrar(u.getUsuarioId(), "criar_pagamento", "pagamento", salvo.getId(), null);
        return PagamentoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscar(Integer id) {
        return PagamentoResponse.de(obter(id));
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorUtilizacao(Integer utilizacaoId) {
        return repository.findByUtilizacaoId(utilizacaoId)
                .stream().map(PagamentoResponse::de).toList();
    }

    public PagamentoResponse alterarEstado(Integer id, EstadoPagamento novo) {
        Pagamento p = obter(id);
        p.setEstado(novo);
        if (novo == EstadoPagamento.aprovado && p.getPagoEm() == null) {
            p.setPagoEm(LocalDateTime.now());
        }
        Pagamento salvo = repository.save(p);
        auditoria.registrar(null, "pagamento_" + novo.name(), "pagamento", salvo.getId(), null);
        return PagamentoResponse.de(salvo);
    }

    private Pagamento obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));
    }

    private ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
