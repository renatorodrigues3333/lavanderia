package br.com.lavamais.lavamais_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.lavamais.lavamais_api.entity.Usuario;
import br.com.lavamais.lavamais_api.repository.UsuarioRepository;

/**
 * Cria o administrador (dono da lavanderia) na primeira vez que a API inicia.
 * Se o e-mail já existir, não mexe em nada (nunca troca a senha).
 */
@Component
public class AdministradorInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdministradorInicializador.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String email;
    private final String nome;
    private final String senha;

    public AdministradorInicializador(UsuarioRepository repository,
                                      PasswordEncoder encoder,
                                      @Value("${lavamais.admin.email}") String email,
                                      @Value("${lavamais.admin.nome:Administrador}") String nome,
                                      @Value("${lavamais.admin.senha:}") String senha) {
        this.repository = repository;
        this.encoder = encoder;
        this.email = email;
        this.nome = nome;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.existsByEmail(email)) {
            return;
        }
        if (senha.length() < 8 || senha.startsWith("TROQUE")) {
            throw new IllegalStateException(
                    "Defina lavamais.admin.senha (mínimo 8 caracteres) no arquivo application-local.properties");
        }
        Usuario admin = new Usuario();
        admin.setNome(nome);
        admin.setEmail(email);
        admin.setSenhaHash(encoder.encode(senha));
        repository.save(admin);
        log.info("Administrador criado: {}", email);
    }
}
