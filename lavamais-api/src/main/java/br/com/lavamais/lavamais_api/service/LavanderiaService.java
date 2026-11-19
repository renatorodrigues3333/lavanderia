package br.com.lavamais.lavamais_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lavamais.lavamais_api.dto.LavanderiaRequest;
import br.com.lavamais.lavamais_api.dto.LavanderiaResponse;
import br.com.lavamais.lavamais_api.entity.Lavanderia;
import br.com.lavamais.lavamais_api.repository.LavanderiaRepository;

@Service
@Transactional
public class LavanderiaService {

    private final LavanderiaRepository repository;

    public LavanderiaService(LavanderiaRepository repository) {
        this.repository = repository;
    }

    /** Enquanto o administrador não cadastrar nada, devolve nome e endereço vazios. */
    @Transactional(readOnly = true)
    public LavanderiaResponse buscar() {
        return repository.findById(Lavanderia.ID_UNICO)
                .map(LavanderiaResponse::de)
                .orElseGet(() -> new LavanderiaResponse("", ""));
    }

    public LavanderiaResponse atualizar(LavanderiaRequest req) {
        Lavanderia l = repository.findById(Lavanderia.ID_UNICO).orElseGet(Lavanderia::new);
        l.setNome(req.nome() == null ? "" : req.nome().trim());
        l.setEndereco(req.endereco().trim());
        return LavanderiaResponse.de(repository.save(l));
    }
}
