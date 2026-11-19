package br.com.lavamais.lavamais_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.dto.HistoricoMaquinaResponse;
import br.com.lavamais.lavamais_api.service.HistoricoMaquinaService;

@RestController
@RequestMapping("/api/maquinas/{maquinaId}/historico")
public class HistoricoMaquinaController {

    private final HistoricoMaquinaService service;

    public HistoricoMaquinaController(HistoricoMaquinaService service) {
        this.service = service;
    }

    @GetMapping
    public List<HistoricoMaquinaResponse> listar(@PathVariable Integer maquinaId) {
        return service.listarPorMaquina(maquinaId);
    }
}
