package br.com.lavamais.lavamais_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.dto.EstadoMaquinaRequest;
import br.com.lavamais.lavamais_api.dto.MaquinaRequest;
import br.com.lavamais.lavamais_api.dto.MaquinaResponse;
import br.com.lavamais.lavamais_api.service.MaquinaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maquinas")
public class MaquinaController {

    private final MaquinaService service;

    public MaquinaController(MaquinaService service) {
        this.service = service;
    }

    @GetMapping
    public List<MaquinaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public MaquinaResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaquinaResponse cadastrar(@Valid @RequestBody MaquinaRequest req) {
        return service.cadastrar(req);
    }

    @PutMapping("/{id}")
    public MaquinaResponse atualizar(@PathVariable Integer id, @Valid @RequestBody MaquinaRequest req) {
        return service.atualizar(id, req);
    }

    @PatchMapping("/{id}/estado")
    public MaquinaResponse alterarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoMaquinaRequest req) {
        return service.alterarEstado(id, req.estado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Integer id) {
        service.desativar(id);
    }
}
