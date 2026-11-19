package br.com.lavamais.lavamais_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import br.com.lavamais.lavamais_api.entity.Pagamento;
import jakarta.persistence.LockModeType;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    List<Pagamento> findByUtilizacaoId(Integer utilizacaoId);
    boolean existsByUtilizacaoIdAndEstado(Integer utilizacaoId, EstadoPagamento estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pagamento p WHERE p.id = :id")
    Optional<Pagamento> findByIdComBloqueio(@Param("id") Integer id);
}
