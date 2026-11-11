package br.com.lavamais.lavamais_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.MaquinaRequest;
import br.com.lavamais.lavamais_api.dto.MaquinaResponse;
import br.com.lavamais.lavamais_api.entity.EstadoMaquina;
import br.com.lavamais.lavamais_api.entity.Maquina;
import br.com.lavamais.lavamais_api.repository.MaquinaRepository;

@Service
@Transactional
public class MaquinaService {

    private final MaquinaRepository repository;
    private final HistoricoMaquinaService historico;

    public MaquinaService(MaquinaRepository repository, HistoricoMaquinaService historico) {
        this.repository = repository;
        this.historico = historico;
    }

    @Transactional(readOnly = true)
    public List<MaquinaResponse> listar() {
        return repository.findByAtivaTrue().stream().map(MaquinaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public MaquinaResponse buscar(Integer id) {
        return MaquinaResponse.de(obter(id));
    }

    public MaquinaResponse cadastrar(MaquinaRequest req) {
        Maquina m = new Maquina();
        preencher(m, req);
        return MaquinaResponse.de(repository.save(m));
    }

    public MaquinaResponse atualizar(Integer id, MaquinaRequest req) {
        Maquina m = obter(id);
        preencher(m, req);
        return MaquinaResponse.de(repository.save(m));
    }

    public MaquinaResponse alterarEstado(Integer id, EstadoMaquina novo) {
        Maquina m = obter(id);
        EstadoMaquina anterior = m.getEstado();
        m.setEstado(novo);
        Maquina salva = repository.save(m);
        historico.registrar(salva.getId(), null, null, "mudanca_estado",
                anterior.name(), novo.name(), null);
        return MaquinaResponse.de(salva);
    }

    public void desativar(Integer id) {
        Maquina m = obter(id);
        m.setAtiva(false);
        repository.save(m);
    }

    private void preencher(Maquina m, MaquinaRequest req) {
        m.setNome(req.nome());
        m.setCapacidade(req.capacidade());
        m.setDescricao(req.descricao());
    }

    private Maquina obter(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
    }
}
