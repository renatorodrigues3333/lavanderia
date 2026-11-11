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
@Table(name = "utilizacao")
public class Utilizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "maquina_id", nullable = false)
    private Integer maquinaId;

    @Column(name = "servico_id", nullable = false)
    private Integer servicoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUtilizacao estado = EstadoUtilizacao.pendente;

    @Column(name = "inicio_previsto")
    private LocalDateTime inicioPrevisto;

    @Column(name = "inicio_real")
    private LocalDateTime inicioReal;

    @Column(name = "fim_real")
    private LocalDateTime fimReal;

    @Column(name = "valor_total", nullable = false, precision = 6, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Integer maquinaId) { this.maquinaId = maquinaId; }
    public Integer getServicoId() { return servicoId; }
    public void setServicoId(Integer servicoId) { this.servicoId = servicoId; }
    public EstadoUtilizacao getEstado() { return estado; }
    public void setEstado(EstadoUtilizacao estado) { this.estado = estado; }
    public LocalDateTime getInicioPrevisto() { return inicioPrevisto; }
    public void setInicioPrevisto(LocalDateTime inicioPrevisto) { this.inicioPrevisto = inicioPrevisto; }
    public LocalDateTime getInicioReal() { return inicioReal; }
    public void setInicioReal(LocalDateTime inicioReal) { this.inicioReal = inicioReal; }
    public LocalDateTime getFimReal() { return fimReal; }
    public void setFimReal(LocalDateTime fimReal) { this.fimReal = fimReal; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
