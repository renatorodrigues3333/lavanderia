package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {
    /** Notificações do usuário e as gerais (usuario_id nulo). */
    List<Notificacao> findByUsuarioIdOrUsuarioIdIsNullOrderByCriadoEmDesc(Integer usuarioId);
}
