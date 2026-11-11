package br.com.lavamais.lavamais_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.lavamais.lavamais_api.dto.HistoricoMaquinaResponse;
import br.com.lavamais.lavamais_api.entity.HistoricoMaquina;
import br.com.lavamais.lavamais_api.repository.HistoricoMaquinaRepository;
import br.com.lavamais.lavamais_api.repository.MaquinaRepository;

@Service
@Transactional
public class HistoricoMaquinaService {

    private final HistoricoMaquinaRepository repository;
    private final MaquinaRepository maquinaRepository;

    public HistoricoMaquinaService(HistoricoMaquinaRepository repository, MaquinaRepository maquinaRepository) {
        this.repository = repository;
        this.maquinaRepository = maquinaRepository;
    }

    public void registrar(Integer maquinaId, Integer usuarioId, Integer utilizacaoId,
                          String tipoEvento, String estadoAnterior, String estadoNovo, String descricao) {
        HistoricoMaquina h = new HistoricoMaquina();
        h.setMaquinaId(maquinaId);
        h.setUsuarioId(usuarioId);
        h.setUtilizacaoId(utilizacaoId);
        h.setTipoEvento(tipoEvento);
        h.setEstadoAnterior(estadoAnterior);
        h.setEstadoNovo(estadoNovo);
        h.setDescricao(descricao);
        repository.save(h);
    }

    @Transactional(readOnly = true)
    public List<HistoricoMaquinaResponse> listarPorMaquina(Integer maquinaId) {
        if (!maquinaRepository.existsById(maquinaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada");
        }
        return repository.findByMaquinaIdOrderByCriadoEmDesc(maquinaId)
                .stream().map(HistoricoMaquinaResponse::de).toList();
    }
}
