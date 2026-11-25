package br.com.lavamais.lavamais_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Utilizacao;
import jakarta.persistence.LockModeType;

public interface UtilizacaoRepository extends JpaRepository<Utilizacao, Integer> {
    List<Utilizacao> findByUsuarioIdOrderByCriadoEmDesc(Integer usuarioId);
    List<Utilizacao> findAllByOrderByCriadoEmDesc();
    List<Utilizacao> findByEstado(EstadoUtilizacao estado);
    boolean existsByUsuarioIdAndEstado(Integer usuarioId, EstadoUtilizacao estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Utilizacao u WHERE u.id = :id")
    Optional<Utilizacao> findByIdComBloqueio(@Param("id") Integer id);
}
