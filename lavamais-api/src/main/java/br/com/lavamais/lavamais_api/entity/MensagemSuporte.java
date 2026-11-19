package br.com.lavamais.lavamais_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "men_suporte")
public class MensagemSuporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "suporte_id", nullable = false)
    private Integer suporteId;

    @Column(name = "remetente_id", nullable = false)
    private Integer remetenteId;

    @Column(nullable = false, columnDefinition = "text")
    private String conteudo;

    @Column(name = "enviada_em", nullable = false, updatable = false)
    private LocalDateTime enviadaEm = LocalDateTime.now();

    @Column(name = "lida_em")
    private LocalDateTime lidaEm;

    public Integer getId() { return id; }
    public Integer getSuporteId() { return suporteId; }
    public void setSuporteId(Integer suporteId) { this.suporteId = suporteId; }
    public Integer getRemetenteId() { return remetenteId; }
    public void setRemetenteId(Integer remetenteId) { this.remetenteId = remetenteId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getEnviadaEm() { return enviadaEm; }
    public void setEnviadaEm(LocalDateTime enviadaEm) { this.enviadaEm = enviadaEm; }
    public LocalDateTime getLidaEm() { return lidaEm; }
    public void setLidaEm(LocalDateTime lidaEm) { this.lidaEm = lidaEm; }
}
