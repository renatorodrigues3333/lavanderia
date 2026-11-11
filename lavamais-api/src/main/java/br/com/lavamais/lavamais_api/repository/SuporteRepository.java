package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Suporte;

public interface SuporteRepository extends JpaRepository<Suporte, Integer> {
    List<Suporte> findByUsuarioIdOrderByCriadoEmDesc(Integer usuarioId);
    List<Suporte> findAllByOrderByCriadoEmDesc();
}
