package br.com.lavamais.lavamais_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.dto.EstadoPagamentoRequest;
import br.com.lavamais.lavamais_api.dto.PagamentoRequest;
import br.com.lavamais.lavamais_api.dto.PagamentoResponse;
import br.com.lavamais.lavamais_api.service.PagamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse criar(@Valid @RequestBody PagamentoRequest req) {
        return service.criar(req);
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @GetMapping
    public List<PagamentoResponse> listarPorUtilizacao(@RequestParam Integer utilizacaoId) {
        return service.listarPorUtilizacao(utilizacaoId);
    }

    @PatchMapping("/{id}/estado")
    public PagamentoResponse alterarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoPagamentoRequest req) {
        return service.alterarEstado(id, req.estado());
    }
}
