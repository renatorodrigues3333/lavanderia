package br.com.lavamais.lavamais_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import br.com.lavamais.lavamais_api.entity.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    List<Pagamento> findByUtilizacaoId(Integer utilizacaoId);
    boolean existsByUtilizacaoIdAndEstado(Integer utilizacaoId, EstadoPagamento estado);
}
