package br.com.lavamais.lavamais_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.ServicoRequest;
import br.com.lavamais.lavamais_api.dto.ServicoResponse;
import br.com.lavamais.lavamais_api.entity.Servico;
import br.com.lavamais.lavamais_api.repository.ServicoRepository;

@Service
@Transactional
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listar() {
        return repository.findByAtivoTrue().stream().map(ServicoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscar(Integer id) {
        return ServicoResponse.de(obter(id));
    }

    public ServicoResponse cadastrar(ServicoRequest req) {
        Servico s = new Servico();
        preencher(s, req);
        return ServicoResponse.de(repository.save(s));
    }

    public ServicoResponse atualizar(Integer id, ServicoRequest req) {
        Servico s = obter(id);
        preencher(s, req);
        return ServicoResponse.de(repository.save(s));
    }

    public void desativar(Integer id) {
        Servico s = obter(id);
        s.setAtivo(false);
        repository.save(s);
    }

    private void preencher(Servico s, ServicoRequest req) {
        s.setNome(req.nome());
        s.setDescricao(req.descricao());
        s.setPreco(req.preco());
        s.setDuracaoEstimada(req.duracaoEstimada());
    }

    private Servico obter(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
    }
}
