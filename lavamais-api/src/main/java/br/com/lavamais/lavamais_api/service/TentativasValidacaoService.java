package br.com.lavamais.lavamais_api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Limita solicitações e tentativas incorretas de recuperação de senha por IP e e-mail. */
@Service
public class TentativasValidacaoService {

    private static final int MAX_SOLICITACOES_IP = 10;
    private static final int MAX_SOLICITACOES_EMAIL = 3;

    private static final int MAX_FALHAS_VALIDACAO_IP = 15;
    private static final int MAX_FALHAS_VALIDACAO_COMBINADA = 5;

    private static final int MAX_FALHAS_REDEFINICAO_IP = 10;

    private static final Duration JANELA = Duration.ofMinutes(15);

    private record Registro(int contador, Instant inicio) {}

    private final Map<String, Registro> solicitacoesIp = new ConcurrentHashMap<>();
    private final Map<String, Registro> solicitacoesEmail = new ConcurrentHashMap<>();

    private final Map<String, Registro> falhasValidacaoIp = new ConcurrentHashMap<>();
    private final Map<String, Registro> falhasValidacaoCombinada = new ConcurrentHashMap<>();

    private final Map<String, Registro> falhasRedefinicaoIp = new ConcurrentHashMap<>();

    /** Verifica e registra limites para solicitação de código de recuperação. */
    public void verificarERegistrarSolicitacao(String ip, String email) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        String chaveEmail = email.toLowerCase().trim();
        Instant agora = Instant.now();

        verificarLimite(solicitacoesIp, "solicitar_ip:" + chaveIp, MAX_SOLICITACOES_IP,
                "Muitas solicitações de recuperação vindas deste dispositivo. Tente novamente em alguns minutos");
        verificarLimite(solicitacoesEmail, "solicitar_email:" + chaveEmail, MAX_SOLICITACOES_EMAIL,
                "Muitas solicitações de recuperação para este e-mail. Tente novamente em alguns minutos");

        incrementar(solicitacoesIp, "solicitar_ip:" + chaveIp, agora);
        incrementar(solicitacoesEmail, "solicitar_email:" + chaveEmail, agora);
    }

    /** Verifica bloqueios para validação de código (por IP e por combinação IP+E-mail). */
    public void verificarBloqueioValidacao(String ip, String email) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        String chaveCombinada = chaveIp + ":" + email.toLowerCase().trim();

        verificarLimite(falhasValidacaoIp, "valida_ip:" + chaveIp, MAX_FALHAS_VALIDACAO_IP,
                "Muitas tentativas incorretas de validação vindas deste dispositivo. Tente novamente em alguns minutos");
        verificarLimite(falhasValidacaoCombinada, "valida_comb:" + chaveCombinada, MAX_FALHAS_VALIDACAO_COMBINADA,
                "Muitas tentativas de validação incorretas para esta conta a partir deste dispositivo. Tente novamente em alguns minutos");
    }

    public void registrarFalhaValidacao(String ip, String email) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        String chaveCombinada = chaveIp + ":" + email.toLowerCase().trim();
        Instant agora = Instant.now();

        incrementar(falhasValidacaoIp, "valida_ip:" + chaveIp, agora);
        incrementar(falhasValidacaoCombinada, "valida_comb:" + chaveCombinada, agora);
    }

    public void limparValidacao(String ip, String email) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        String chaveCombinada = chaveIp + ":" + email.toLowerCase().trim();
        falhasValidacaoCombinada.remove("valida_comb:" + chaveCombinada);
    }

    /** Verifica bloqueios para redefinição de senha por IP. */
    public void verificarBloqueioRedefinicao(String ip) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        verificarLimite(falhasRedefinicaoIp, "redefine_ip:" + chaveIp, MAX_FALHAS_REDEFINICAO_IP,
                "Muitas tentativas incorretas de redefinição de senha. Tente novamente em alguns minutos");
    }

    public void registrarFalhaRedefinicao(String ip) {
        String chaveIp = ip != null ? ip.trim() : "desconhecido";
        incrementar(falhasRedefinicaoIp, "redefine_ip:" + chaveIp, Instant.now());
    }

    private void verificarLimite(Map<String, Registro> mapa, String chave, int max, String mensagemErro) {
        Registro r = mapa.get(chave);
        if (r == null) {
            return;
        }
        if (r.inicio().plus(JANELA).isBefore(Instant.now())) {
            mapa.remove(chave);
            return;
        }
        if (r.contador() >= max) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, mensagemErro);
        }
    }

    private void incrementar(Map<String, Registro> mapa, String chave, Instant agora) {
        mapa.merge(chave, new Registro(1, agora), (atual, novo) ->
                atual.inicio().plus(JANELA).isBefore(agora)
                        ? novo
                        : new Registro(atual.contador() + 1, atual.inicio()));
        if (mapa.size() > 10_000) {
            mapa.values().removeIf(r -> r.inicio().plus(JANELA).isBefore(agora));
        }
    }
}
