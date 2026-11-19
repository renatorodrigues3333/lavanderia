package com.equipe.lavemais

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.util.Date

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
    const val ADMIN = "admin"
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
        popUpTo(graph.id) { inclusive = true }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val maquinasVm: MaquinasViewModel = viewModel()
    val pedidoVm: PedidoViewModel = viewModel()
    val adminVm: AdminViewModel = viewModel()
    val lavanderiaVm: LavanderiaViewModel = viewModel()
    val suporteVm: SuporteViewModel = viewModel()

    val sessaoExpirada: () -> Unit = {
        pedidoVm.limpar()
        adminVm.limpar()
        suporteVm.limpar()
        navController.irParaLogin()
    }

    // Erros de rede/servidor das telas que não têm área de erro própria
    val context = LocalContext.current
    val erroAtual = pedidoVm.erro ?: maquinasVm.erro
    LaunchedEffect(erroAtual) {
        erroAtual?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    // Calculado uma vez: mudar o startDestination depois recriaria o grafo de navegação
    val inicio = remember {
        when {
            !authVm.logado -> Rotas.LOGIN
            authVm.administrador -> Rotas.ADMIN
            else -> Rotas.HOME
        }
    }

    CompositionLocalProvider(LocalLavanderia provides lavanderiaVm.dados) {
    NavHost(navController = navController, startDestination = inicio) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                carregando = authVm.carregando,
                erro = authVm.erro,
                onEntrar = { login, senha ->
                    authVm.entrar(login, senha) {
                        // O administrador cai direto no painel dele
                        navController.navigate(if (authVm.administrador) Rotas.ADMIN else Rotas.HOME) {
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
                onCadastrar = { nome, email, telefone, senha ->
                    authVm.cadastrar(nome, email, telefone, senha) {
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
                lavanderiaVm.carregar(sessaoExpirada)
                maquinasVm.carregar(sessaoExpirada)
            }
            HomeScreen(
                nome = authVm.nome,
                livres = maquinasVm.livres,
                emUso = maquinasVm.emUso,
                foraServico = maquinasVm.foraDeServico,
                onNavegar = { navController.abaPara(it) }
            )
        }

        // ---------- Painel do administrador ----------

        composable(Rotas.ADMIN) {
            LaunchedEffect(Unit) {
                adminVm.carregar(sessaoExpirada)
                lavanderiaVm.carregar(sessaoExpirada)
            }
            AdminScreen(
                nome = authVm.nome,
                maquinas = adminVm.maquinas,
                servicos = adminVm.servicos,
                lavanderia = lavanderiaVm.dados,
                salvandoLavanderia = lavanderiaVm.salvando,
                carregando = adminVm.carregando,
                mensagem = adminVm.mensagem ?: lavanderiaVm.mensagem,
                onMensagemExibida = {
                    adminVm.limparMensagem()
                    lavanderiaVm.limparMensagem()
                },
                onAtualizar = {
                    adminVm.carregar(sessaoExpirada)
                    lavanderiaVm.carregar(sessaoExpirada)
                },
                onSalvarMaquina = { id, nome, capacidade, descricao ->
                    adminVm.salvarMaquina(id, nome, capacidade, descricao, sessaoExpirada)
                },
                onEstadoMaquina = { id, estado -> adminVm.alterarEstadoMaquina(id, estado, sessaoExpirada) },
                onExcluirMaquina = { adminVm.excluirMaquina(it, sessaoExpirada) },
                onSalvarServico = { id, nome, descricao, preco, duracao ->
                    adminVm.salvarServico(id, nome, descricao, preco, duracao, sessaoExpirada)
                },
                onExcluirServico = { adminVm.excluirServico(it, sessaoExpirada) },
                onSalvarLavanderia = { nome, endereco -> lavanderiaVm.salvar(nome, endereco, sessaoExpirada) },
                onSair = {
                    authVm.sair {
                        adminVm.limpar()
                        pedidoVm.limpar()
                        navController.irParaLogin()
                    }
                }
            )
        }

        // ---------- Fluxo do pedido ----------

        composable(Rotas.MAQUINAS) {
            LaunchedEffect(Unit) {
                maquinasVm.carregar(sessaoExpirada)
                pedidoVm.carregarServicos(sessaoExpirada)
            }
            // Mantém a escolha anterior se a máquina continuar livre; senão sugere a primeira livre
            val livres = maquinasVm.maquinas.filter { it.status() == MachineStatus.LIVRE }
            val selecionada = livres.firstOrNull { it.id == pedidoVm.maquinaSelecionada?.id }
                ?: livres.firstOrNull()

            MaquinasScreen(
                maquinas = maquinasVm.maquinas,
                servicos = pedidoVm.servicos,
                maquinaSelecionadaId = selecionada?.id,
                onSelecionar = { pedidoVm.selecionarMaquina(it) },
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = {
                    if (selecionada != null) {
                        pedidoVm.selecionarMaquina(selecionada)
                        navController.navigate(Rotas.SERVICO)
                    }
                }
            )
        }

        composable(Rotas.SERVICO) {
            LaunchedEffect(Unit) {
                if (pedidoVm.servicos.isEmpty()) pedidoVm.carregarServicos(sessaoExpirada)
            }
            val servico = pedidoVm.servicoSelecionado
            ServicoScreen(
                maquina = pedidoVm.maquinaSelecionada,
                servicos = pedidoVm.servicos,
                servicoSelecionadoId = servico?.id,
                onSelecionar = { pedidoVm.selecionarServico(it) },
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = {
                    if (servico != null) {
                        pedidoVm.selecionarServico(servico)
                        navController.navigate(Rotas.RESUMO)
                    }
                }
            )
        }

        composable(Rotas.RESUMO) {
            ResumoScreen(
                maquina = pedidoVm.maquinaSelecionada,
                servico = pedidoVm.servicoSelecionado,
                enviando = pedidoVm.carregando,
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onConfirmar = {
                    pedidoVm.confirmarPedido(sessaoExpirada) {
                        // Tira máquina/serviço/resumo da pilha para não criar o pedido duas vezes
                        navController.navigate(Rotas.PAGAMENTO) {
                            popUpTo(Rotas.MAQUINAS) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Rotas.PAGAMENTO) {
            val maquina = pedidoVm.pedidoAtual?.maquina ?: pedidoVm.maquinaSelecionada?.identificacao.orEmpty()
            val servico = pedidoVm.servicoSelecionado
            val valor = pedidoVm.pedidoAtual?.valor_total ?: servico?.preco ?: 0.0
            val duracao = pedidoVm.pedidoAtual?.duracao_estimada ?: servico?.duracao_estimada ?: 0
            val nomeServico = pedidoVm.pedidoAtual?.servico ?: servico?.nome.orEmpty()

            PagamentoScreen(
                codigo = pedidoVm.pedidoId?.let { codigoPedido(it) } ?: "",
                valor = valor,
                descricao = "$maquina · $nomeServico · $duracao min",
                enviando = pedidoVm.carregando,
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onContinuar = { metodo ->
                    pedidoVm.pagar(metodo, sessaoExpirada) {
                        // Pedido pago não volta para a tela de pagamento
                        navController.navigate(Rotas.ACOMPANHAMENTO) {
                            popUpTo(Rotas.PAGAMENTO) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Rotas.ACOMPANHAMENTO) {
            // Atualiza o pedido ao abrir e o relógio a cada 30 s
            var agora by remember { mutableLongStateOf(System.currentTimeMillis()) }
            LaunchedEffect(Unit) {
                pedidoVm.carregarPedidos(sessaoExpirada)
                while (true) {
                    delay(30_000)
                    agora = System.currentTimeMillis()
                }
            }

            val pedido = pedidoVm.pedidoAtual
            val servico = pedidoVm.servicoSelecionado
            val duracao = pedido?.duracao_estimada ?: servico?.duracao_estimada ?: 0
            // decorrido_seg vem do servidor; somamos o tempo passado desde que a lista chegou
            val decorridoSeg = pedido?.decorrido_seg?.let { seg ->
                seg + (agora - pedidoVm.pedidosRecebidosEm) / 1000
            }
            val decorrido = decorridoSeg?.let { (it / 60).toInt() } ?: 0
            val termino = decorridoSeg?.let {
                horarioPrevisto(Date(agora + (duracao * 60L - it).coerceAtLeast(0) * 1000))
            } ?: "—"

            AcompanhamentoScreen(
                codigo = pedidoVm.pedidoId?.let { codigoPedido(it) } ?: "",
                maquina = pedido?.maquina ?: pedidoVm.maquinaSelecionada?.identificacao.orEmpty(),
                servico = pedido?.servico ?: servico?.nome.orEmpty(),
                estado = pedido?.let { estadoTexto(it.estado) } ?: "Em funcionamento",
                valor = pedido?.valor_total ?: servico?.preco ?: 0.0,
                duracaoMin = duracao,
                decorridoMin = decorrido,
                terminoPrevisto = termino,
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.HISTORICO) {
            LaunchedEffect(Unit) {
                pedidoVm.carregarPedidos(sessaoExpirada)
            }
            HistoricoScreen(
                pedidos = pedidoVm.pedidos,
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onUsarReferencia = { pedido ->
                    // Só pré-seleciona máquina e serviço; nada é criado antes de "Confirmar"
                    pedidoVm.usarComoReferencia(pedido)
                    navController.navigate(Rotas.MAQUINAS) { launchSingleTop = true }
                }
            )
        }

        composable(Rotas.PERFIL) {
            LaunchedEffect(Unit) {
                suporteVm.limparErro()
                authVm.carregarPerfil(sessaoExpirada)
                suporteVm.carregarSolicitacoes(sessaoExpirada)
                pedidoVm.carregarPedidos(sessaoExpirada)
            }
            PerfilScreen(
                nome = authVm.nome,
                email = authVm.email,
                telefone = authVm.usuario?.telefone,
                salvandoPerfil = authVm.salvandoPerfil,
                erroPerfil = authVm.erroPerfil,
                pedidos = pedidoVm.pedidos,
                solicitacoes = suporteVm.solicitacoes,
                notificacoes = suporteVm.notificacoes,
                enviandoSolicitacao = suporteVm.enviando,
                erroSolicitacao = suporteVm.erro,
                onEnviarSolicitacao = { tema, descricao, limpar ->
                    suporteVm.abrirSolicitacao(tema, descricao, sessaoExpirada) {
                        limpar()
                        Toast.makeText(context, "Solicitação enviada. Acompanhe o andamento abaixo.", Toast.LENGTH_SHORT).show()
                    }
                },
                onAbrirNotificacoes = { suporteVm.carregarNotificacoes(sessaoExpirada) },
                onLerNotificacao = { suporteVm.lerNotificacao(it, sessaoExpirada) },
                onSolicitarExclusao = { concluir ->
                    suporteVm.solicitarExclusaoConta(sessaoExpirada) {
                        concluir()
                        Toast.makeText(context, "Solicitação de exclusão enviada à equipe.", Toast.LENGTH_LONG).show()
                    }
                },
                onAbrirEdicao = { authVm.limparErroPerfil() },
                onSalvarPerfil = { dados, fechar ->
                    authVm.atualizarPerfil(
                        nome = dados.nome,
                        email = dados.email,
                        telefone = dados.telefone,
                        senhaAtual = dados.senhaAtual,
                        novaSenha = dados.novaSenha,
                        aoExpirar = sessaoExpirada
                    ) {
                        fechar()
                        Toast.makeText(context, "Dados atualizados com sucesso.", Toast.LENGTH_SHORT).show()
                    }
                },
                onNavegar = { navController.abaPara(it) },
                onVoltar = { navController.popBackStack() },
                onSair = {
                    authVm.sair {
                        pedidoVm.limpar()
                        suporteVm.limpar()
                        navController.irParaLogin()
                    }
                }
            )
        }
    }
    }
}
