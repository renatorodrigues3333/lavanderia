package com.equipe.lavemais

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Rotas {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val HOME = "home"
    const val MAQUINAS = "maquinas"
    const val SERVICO = "servico"
    const val RESUMO = "resumo"
    const val PAGAMENTO = "pagamento"
    const val ACOMPANHAMENTO = "acompanhamento"
    const val HISTORICO = "historico"
    const val PERFIL = "perfil"
}

/** Navegação da barra inferior: Início / Máquinas / Pedidos / Perfil. */
private fun NavController.abaPara(rota: String) {
    if (rota == Rotas.HOME) {
        popBackStack(Rotas.HOME, false)
    } else {
        navigate(rota) {
            popUpTo(Rotas.HOME)
            launchSingleTop = true
        }
    }
}

/** Volta para o login limpando todas as telas anteriores. */
private fun NavController.irParaLogin() {
    navigate(Rotas.LOGIN) {
        popUpTo(Rotas.HOME) { inclusive = true }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val maquinasVm: MaquinasViewModel = viewModel()

    val inicio = if (authVm.logado) Rotas.HOME else Rotas.LOGIN

    NavHost(navController = navController, startDestination = inicio) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                carregando = authVm.carregando,
                erro = authVm.erro,
                onEntrar = { login, senha ->
                    authVm.entrar(login, senha) {
                        navController.navigate(Rotas.HOME) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onCriarConta = {
                    authVm.limparErro()
                    navController.navigate(Rotas.CADASTRO)
                }
            )
        }

        composable(Rotas.CADASTRO) {
            CadastroScreen(
                carregando = authVm.carregando,
                erroServidor = authVm.erro,
                onCadastrar = { nome, email, telefone, cpf, senha ->
                    authVm.cadastrar(nome, email, telefone, cpf, senha) {
                        navController.navigate(Rotas.HOME) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onVoltar = {
                    authVm.limparErro()
                    navController.popBackStack()
                },
                onEntrar = {
                    authVm.limparErro()
                    navController.popBackStack()
                }
            )
        }

        composable(Rotas.HOME) {
            LaunchedEffect(Unit) {
                maquinasVm.carregar { navController.irParaLogin() }
            }
            HomeScreen(
                nome = authVm.nome,
                livres = maquinasVm.livres,
                emUso = maquinasVm.emUso,
                foraServico = maquinasVm.foraDeServico,
                onNavegar = { navController.abaPara(it) }
            )
        }

        // Fluxo do pedido (dados ainda estáticos nesta fase)
        composable(Rotas.MAQUINAS) {
            MaquinasScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rotas.SERVICO) }
            )
        }

        composable(Rotas.SERVICO) {
            ServicoScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rotas.RESUMO) }
            )
        }

        composable(Rotas.RESUMO) {
            ResumoScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onConfirmar = { navController.navigate(Rotas.PAGAMENTO) }
            )
        }

        composable(Rotas.PAGAMENTO) {
            PagamentoScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rotas.ACOMPANHAMENTO) }
            )
        }

        composable(Rotas.ACOMPANHAMENTO) {
            AcompanhamentoScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.HISTORICO) {
            HistoricoScreen(
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.PERFIL) {
            PerfilScreen(
                nome = authVm.nome,
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onSair = { authVm.sair { navController.irParaLogin() } }
            )
        }
    }
}
