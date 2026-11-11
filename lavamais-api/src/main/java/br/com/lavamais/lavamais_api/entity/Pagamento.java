package br.com.lavamais.lavamais_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "utilizacao_id", nullable = false)
    private Integer utilizacaoId;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Column(length = 35)
    private String provedor;

    @Column(name = "referencia_externa", length = 255)
    private String referenciaExterna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPagamento estado = EstadoPagamento.pendente;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    public Integer getId() { return id; }
    public Integer getUtilizacaoId() { return utilizacaoId; }
    public void setUtilizacaoId(Integer utilizacaoId) { this.utilizacaoId = utilizacaoId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }
    public String getProvedor() { return provedor; }
    public void setProvedor(String provedor) { this.provedor = provedor; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }
    public EstadoPagamento getEstado() { return estado; }
    public void setEstado(EstadoPagamento estado) { this.estado = estado; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getPagoEm() { return pagoEm; }
    public void setPagoEm(LocalDateTime pagoEm) { this.pagoEm = pagoEm; }
}
