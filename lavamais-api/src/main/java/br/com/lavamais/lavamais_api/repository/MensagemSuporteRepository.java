package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.MensagemSuporte;

public interface MensagemSuporteRepository extends JpaRepository<MensagemSuporte, Integer> {
    List<MensagemSuporte> findBySuporteIdOrderByEnviadaEmAsc(Integer suporteId);
}
