package br.com.lavamais.lavamais_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lavamais.lavamais_api.config.SessaoUsuario;
import br.com.lavamais.lavamais_api.dto.LoginRequest;
import br.com.lavamais.lavamais_api.dto.LoginResponse;
import br.com.lavamais.lavamais_api.dto.MensagemResponse;
import br.com.lavamais.lavamais_api.dto.RedefinirSenhaRequest;
import br.com.lavamais.lavamais_api.dto.SolicitarRecuperacaoRequest;
import br.com.lavamais.lavamais_api.dto.UsuarioLogadoResponse;
import br.com.lavamais.lavamais_api.dto.ValidarCodigoRequest;
import br.com.lavamais.lavamais_api.dto.ValidarCodigoResponse;
import br.com.lavamais.lavamais_api.service.AuthService;
import br.com.lavamais.lavamais_api.service.RecuperacaoSenhaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public AuthController(AuthService service, RecuperacaoSenhaService recuperacaoSenhaService) {
        this.service = service;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return service.login(req, obterIpCliente(http));
    }

    /** Dados de quem está logado (o app usa para saber o nome e se é administrador). */
    @GetMapping("/me")
    public UsuarioLogadoResponse me() {
        return service.perfil(SessaoUsuario.id());
    }

    @PostMapping("/recuperar-senha/solicitar")
    public MensagemResponse solicitarRecuperacao(@Valid @RequestBody SolicitarRecuperacaoRequest req, HttpServletRequest http) {
        return recuperacaoSenhaService.solicitar(req, obterIpCliente(http));
    }

    @PostMapping("/recuperar-senha/validar")
    public ValidarCodigoResponse validarCodigo(@Valid @RequestBody ValidarCodigoRequest req, HttpServletRequest http) {
        return recuperacaoSenhaService.validarCodigo(req, obterIpCliente(http));
    }

    @PostMapping("/recuperar-senha/redefinir")
    public MensagemResponse redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest req, HttpServletRequest http) {
        return recuperacaoSenhaService.redefinirSenha(req, obterIpCliente(http));
    }

    private String obterIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
