package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Maquina;

public interface MaquinaRepository extends JpaRepository<Maquina, Integer> {
    List<Maquina> findByAtivaTrue();
}
