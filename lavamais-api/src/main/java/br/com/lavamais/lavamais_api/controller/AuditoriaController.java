package br.com.lavamais.lavamais_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.dto.AuditoriaResponse;
import br.com.lavamais.lavamais_api.service.AuditoriaService;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditoriaResponse> listar(@RequestParam(required = false) Integer usuarioId) {
        return service.listar(usuarioId);
    }
}
