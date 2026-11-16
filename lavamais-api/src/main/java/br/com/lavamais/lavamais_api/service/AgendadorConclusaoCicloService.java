package br.com.lavamais.lavamais_api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Utilizacao;
import br.com.lavamais.lavamais_api.repository.UtilizacaoRepository;

/** Verificação em segundo plano para concluir utilizações em andamento cujos ciclos expiraram. */
@Service
public class AgendadorConclusaoCicloService {

    private static final Logger log = LoggerFactory.getLogger(AgendadorConclusaoCicloService.class);

    private final UtilizacaoRepository utilizacaoRepository;
    private final UtilizacaoService utilizacaoService;

    public AgendadorConclusaoCicloService(UtilizacaoRepository utilizacaoRepository,
                                         UtilizacaoService utilizacaoService) {
        this.utilizacaoRepository = utilizacaoRepository;
        this.utilizacaoService = utilizacaoService;
    }

    /** Executa a cada 30 segundos para verificar e concluir utilidades vencidas. */
    @Scheduled(fixedDelay = 30000)
    public void verificarEConcluirCiclosVencidos() {
        List<Utilizacao> emAndamento = utilizacaoRepository.findByEstado(EstadoUtilizacao.em_andamento);
        if (emAndamento.isEmpty()) {
            return;
        }

        for (Utilizacao u : emAndamento) {
            try {
                boolean concluida = utilizacaoService.processarConclusaoAutomatica(u.getId());
                if (concluida) {
                    log.info("Utilização #{} concluída automaticamente com sucesso", u.getId());
                }
            } catch (Exception e) {
                log.error("Erro ao processar conclusão automática da utilização #{}: {}", u.getId(), e.getMessage());
            }
        }
    }
}
