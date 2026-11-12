package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Utilizacao;

public interface UtilizacaoRepository extends JpaRepository<Utilizacao, Integer> {
    List<Utilizacao> findByUsuarioIdOrderByCriadoEmDesc(Integer usuarioId);
    List<Utilizacao> findAllByOrderByCriadoEmDesc();
}
