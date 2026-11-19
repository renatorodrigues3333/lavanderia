package br.com.lavamais.lavamais_api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Limita tentativas de login erradas: 5 falhas por e-mail+IP bloqueiam por 15 minutos. */
@Service
public class TentativasLoginService {

    private static final int MAXIMO_FALHAS = 5;
    private static final Duration JANELA = Duration.ofMinutes(15);

    private record Registro(int falhas, Instant inicio) {}

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    public void verificarBloqueio(String chave) {
        Registro r = registros.get(chave);
        if (r == null) {
            return;
        }
        if (r.inicio().plus(JANELA).isBefore(Instant.now())) {
            registros.remove(chave);
            return;
        }
        if (r.falhas() >= MAXIMO_FALHAS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas. Tente novamente em alguns minutos");
        }
    }

    public void registrarFalha(String chave) {
        Instant agora = Instant.now();
        registros.merge(chave, new Registro(1, agora), (atual, novo) ->
                atual.inicio().plus(JANELA).isBefore(agora)
                        ? novo
                        : new Registro(atual.falhas() + 1, atual.inicio()));
        if (registros.size() > 10_000) {
            registros.values().removeIf(r -> r.inicio().plus(JANELA).isBefore(agora));
        }
    }

    public void limpar(String chave) {
        registros.remove(chave);
    }
}
