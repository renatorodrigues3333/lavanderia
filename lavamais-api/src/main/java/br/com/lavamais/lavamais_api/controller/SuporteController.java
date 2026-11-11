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

import br.com.lavamais.lavamais_api.dto.EstadoSuporteRequest;
import br.com.lavamais.lavamais_api.dto.MensagemSuporteRequest;
import br.com.lavamais.lavamais_api.dto.MensagemSuporteResponse;
import br.com.lavamais.lavamais_api.dto.SuporteRequest;
import br.com.lavamais.lavamais_api.dto.SuporteResponse;
import br.com.lavamais.lavamais_api.service.SuporteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suportes")
public class SuporteController {

    private final SuporteService service;

    public SuporteController(SuporteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuporteResponse abrir(@Valid @RequestBody SuporteRequest req) {
        return service.abrir(req);
    }

    @GetMapping
    public List<SuporteResponse> listar(@RequestParam(required = false) Integer usuarioId) {
        return service.listar(usuarioId);
    }

    @GetMapping("/{id}")
    public SuporteResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PatchMapping("/{id}/estado")
    public SuporteResponse alterarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoSuporteRequest req) {
        return service.alterarEstado(id, req.estado());
    }

    @PostMapping("/{id}/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    public MensagemSuporteResponse enviarMensagem(@PathVariable Integer id, @Valid @RequestBody MensagemSuporteRequest req) {
        return service.enviarMensagem(id, req);
    }

    @GetMapping("/{id}/mensagens")
    public List<MensagemSuporteResponse> listarMensagens(@PathVariable Integer id) {
        return service.listarMensagens(id);
    }

    @PatchMapping("/mensagens/{mensagemId}/lida")
    public MensagemSuporteResponse marcarMensagemLida(@PathVariable Integer mensagemId) {
        return service.marcarMensagemLida(mensagemId);
    }
}
