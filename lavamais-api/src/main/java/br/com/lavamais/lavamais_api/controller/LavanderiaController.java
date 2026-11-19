package br.com.lavamais.lavamais_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.dto.LavanderiaRequest;
import br.com.lavamais.lavamais_api.dto.LavanderiaResponse;
import br.com.lavamais.lavamais_api.service.LavanderiaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lavanderia")
public class LavanderiaController {

    private final LavanderiaService service;

    public LavanderiaController(LavanderiaService service) {
        this.service = service;
    }

    /** Qualquer usuário logado pode ver o endereço. */
    @GetMapping
    public LavanderiaResponse buscar() {
        return service.buscar();
    }

    /** Só o administrador altera (regra em SegurancaConfig). */
    @PutMapping
    public LavanderiaResponse atualizar(@Valid @RequestBody LavanderiaRequest req) {
        return service.atualizar(req);
    }
}
