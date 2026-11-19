package br.com.lavamais.lavamais_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Dados da lavanderia (nome da unidade e endereço). Há um único registro, com id 1. */
@Entity
@Table(name = "lavanderia")
public class Lavanderia {

    public static final Integer ID_UNICO = 1;

    @Id
    private Integer id = ID_UNICO;

    @Column(nullable = false, length = 60)
    private String nome = "";

    @Column(nullable = false, length = 200)
    private String endereco = "";

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
