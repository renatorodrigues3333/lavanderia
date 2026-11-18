package br.com.lavamais.lavamais_api.service;

public interface EmailService {
    void enviarCodigoRecuperacao(String paraEmail, String codigo);
}
