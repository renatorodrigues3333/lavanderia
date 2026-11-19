package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    List<Servico> findByAtivoTrue();
}
