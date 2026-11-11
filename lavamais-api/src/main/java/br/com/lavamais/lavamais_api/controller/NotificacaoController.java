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

import br.com.lavamais.lavamais_api.dto.NotificacaoRequest;
import br.com.lavamais.lavamais_api.dto.NotificacaoResponse;
import br.com.lavamais.lavamais_api.service.NotificacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificacaoResponse criar(@Valid @RequestBody NotificacaoRequest req) {
        return service.criar(req);
    }

    @GetMapping
    public List<NotificacaoResponse> listarPorUsuario(@RequestParam Integer usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @PatchMapping("/{id}/lida")
    public NotificacaoResponse marcarLida(@PathVariable Integer id) {
        return service.marcarLida(id);
    }
}
