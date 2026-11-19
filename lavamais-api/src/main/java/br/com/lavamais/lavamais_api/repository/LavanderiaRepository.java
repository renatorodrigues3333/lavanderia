package br.com.lavamais.lavamais_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Lavanderia;

public interface LavanderiaRepository extends JpaRepository<Lavanderia, Integer> {
}
