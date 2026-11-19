package br.com.lavamais.lavamais_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lavamais.lavamais_api.dto.AuditoriaResponse;
import br.com.lavamais.lavamais_api.entity.Auditoria;
import br.com.lavamais.lavamais_api.repository.AuditoriaRepository;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository repository;

    public AuditoriaService(AuditoriaRepository repository) {
        this.repository = repository;
    }

    /** Registra uma ação importante. usuarioId pode ser nulo quando não se sabe quem fez. */
    public void registrar(Integer usuarioId, String acao, String entidade, Integer entidadeId, String detalhes) {
        Auditoria a = new Auditoria();
        a.setUsuarioId(usuarioId);
        a.setAcao(acao);
        a.setEntidade(entidade);
        a.setEntidadeId(entidadeId);
        a.setDetalhes(detalhes);
        repository.save(a);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listar(Integer usuarioId) {
        List<Auditoria> lista = usuarioId == null
                ? repository.findTop100ByOrderByCriadoEmDesc()
                : repository.findTop100ByUsuarioIdOrderByCriadoEmDesc(usuarioId);
        return lista.stream().map(AuditoriaResponse::de).toList();
    }
}
