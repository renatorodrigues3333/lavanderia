package br.com.lavamais.lavamais_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.LoginRequest;
import br.com.lavamais.lavamais_api.dto.LoginResponse;
import br.com.lavamais.lavamais_api.dto.UsuarioLogadoResponse;
import br.com.lavamais.lavamais_api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return service.login(req, http.getRemoteAddr());
    }

    /** Dados de quem está logado (o app usa para saber o nome e se é administrador). */
    @GetMapping("/me")
    public UsuarioLogadoResponse me() {
        return service.perfil(SessaoUsuario.id());
    }
}
