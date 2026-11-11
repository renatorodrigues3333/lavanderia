package br.com.lavamais.lavamais_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historico_maquina")
public class HistoricoMaquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "maquina_id", nullable = false)
    private Integer maquinaId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "utilizacao_id")
    private Integer utilizacaoId;

    @Column(name = "tipo_evento", nullable = false, length = 40)
    private String tipoEvento;

    @Column(name = "estado_anterior", length = 20)
    private String estadoAnterior;

    @Column(name = "estado_novo", length = 20)
    private String estadoNovo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public Integer getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Integer maquinaId) { this.maquinaId = maquinaId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getUtilizacaoId() { return utilizacaoId; }
    public void setUtilizacaoId(Integer utilizacaoId) { this.utilizacaoId = utilizacaoId; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNovo() { return estadoNovo; }
    public void setEstadoNovo(String estadoNovo) { this.estadoNovo = estadoNovo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
