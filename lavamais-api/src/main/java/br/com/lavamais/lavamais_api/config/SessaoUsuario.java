package br.com.lavamais.lavamais_api.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

/** Dá acesso a quem está logado (vem do token), nunca de dados enviados pelo app. */
public final class SessaoUsuario {

    private SessaoUsuario() {
    }

    private static Authentication autenticacao() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }
        return a;
    }

    public static Integer id() {
        Object principal = autenticacao().getPrincipal();
        if (principal instanceof Jwt jwt) {
            try {
                return Integer.valueOf(jwt.getSubject());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
    }

    public static boolean admin() {
        return autenticacao().getAuthorities().stream()
                .anyMatch(g -> "ROLE_ADMIN".equals(g.getAuthority()));
    }

    public static void exigirAdmin() {
        if (!admin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }

    public static void exigirDonoOuAdmin(Integer donoId) {
        if (!admin() && !id().equals(donoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }
}
