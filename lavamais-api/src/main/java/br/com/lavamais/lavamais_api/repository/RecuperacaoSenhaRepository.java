package br.com.lavamais.lavamais_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.RecuperacaoSenha;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Integer> {
    Optional<RecuperacaoSenha> findFirstByUsuarioIdOrderByCriadoEmDesc(Integer usuarioId);
}
