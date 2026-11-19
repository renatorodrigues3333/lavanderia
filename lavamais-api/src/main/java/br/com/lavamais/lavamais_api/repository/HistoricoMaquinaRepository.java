package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.HistoricoMaquina;

public interface HistoricoMaquinaRepository extends JpaRepository<HistoricoMaquina, Integer> {
    List<HistoricoMaquina> findByMaquinaIdOrderByCriadoEmDesc(Integer maquinaId);
}
