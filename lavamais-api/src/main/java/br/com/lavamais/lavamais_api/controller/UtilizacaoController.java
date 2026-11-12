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

import br.com.lavamais.lavamais_api.dto.UtilizacaoRequest;
import br.com.lavamais.lavamais_api.dto.UtilizacaoResponse;
import br.com.lavamais.lavamais_api.service.UtilizacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/utilizacoes")
public class UtilizacaoController {

    private final UtilizacaoService service;

    public UtilizacaoController(UtilizacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilizacaoResponse criar(@Valid @RequestBody UtilizacaoRequest req) {
        return service.criar(req);
    }

    @GetMapping("/{id}")
    public UtilizacaoResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    /** Cliente: lista as próprias (o parâmetro é ignorado). Administrador: filtra por usuarioId ou lista todas. */
    @GetMapping
    public List<UtilizacaoResponse> listar(@RequestParam(required = false) Integer usuarioId) {
        return service.listar(usuarioId);
    }

    @PatchMapping("/{id}/iniciar")
    public UtilizacaoResponse iniciar(@PathVariable Integer id) {
        return service.iniciar(id);
    }

    @PatchMapping("/{id}/concluir")
    public UtilizacaoResponse concluir(@PathVariable Integer id) {
        return service.concluir(id);
    }

    @PatchMapping("/{id}/cancelar")
    public UtilizacaoResponse cancelar(@PathVariable Integer id) {
        return service.cancelar(id);
    }
}
