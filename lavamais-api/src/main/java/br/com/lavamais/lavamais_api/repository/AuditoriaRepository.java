package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findTop100ByOrderByCriadoEmDesc();
    List<Auditoria> findTop100ByUsuarioIdOrderByCriadoEmDesc(Integer usuarioId);
}
