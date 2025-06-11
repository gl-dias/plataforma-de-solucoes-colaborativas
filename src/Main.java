import bd.ConnectionFactory;
import dao.*;
import modelo.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = new ConnectionFactory().recuperaConexao()) {
            // Inicialização dos DAOs
            PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO(connection);
            UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
            ProjetoDAO projetoDAO = new ProjetoDAO(connection);
            TarefaDAO tarefaDAO = new TarefaDAO(connection);
            SolucaoDAO solucaoDAO = new SolucaoDAO(connection);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(connection);

            try {
                // Criação das tabelas no banco de dados
                perfilUsuarioDAO.criarTabela();
                usuarioDAO.criarTabela();
                projetoDAO.criarTabela();
                tarefaDAO.criarTabela();
                solucaoDAO.criarTabela();
                avaliacaoDAO.criarTabela();

                System.out.println("\n--- Testes de Criação ---");

                // Criação dos perfis de usuário
                PerfilUsuario perfil1 = new PerfilUsuario();
                perfil1.setBiografia("Analista de sistemas com especialização em design de software");
                perfil1.setFotoPerfilUri("http://example.com/analista.jpg");
                perfil1.adicionarHabilidade("UML");
                perfil1.adicionarHabilidade("Análise de Requisitos");
                perfil1.adicionarHabilidade("Scrum");
                perfilUsuarioDAO.salvar(perfil1);
                System.out.println("Perfil criado: " + perfil1);

                PerfilUsuario perfil2 = new PerfilUsuario();
                perfil2.setBiografia("Desenvolvedor mobile sênior com experiência em iOS e Android");
                perfil2.setFotoPerfilUri("http://example.com/mobile-dev.jpg");
                perfil2.adicionarHabilidade("Swift");
                perfil2.adicionarHabilidade("Kotlin");
                perfil2.adicionarHabilidade("Flutter");
                perfilUsuarioDAO.salvar(perfil2);
                System.out.println("Perfil criado: " + perfil2);

                // Criação dos usuários
                Usuario usuario1 = new Usuario();
                usuario1.setNome("Pedro Almeida");
                usuario1.setEmail("pedro@company.com");
                usuario1.setSenhaCriptografada("pass123secure");
                usuario1.setPerfil(perfil1);
                usuarioDAO.salvar(usuario1);
                System.out.println("Usuário criado: " + usuario1);

                Usuario usuario2 = new Usuario();
                usuario2.setNome("Camila Ferreira");
                usuario2.setEmail("camila@company.com");
                usuario2.setSenhaCriptografada("mobile2025#");
                usuario2.setPerfil(perfil2);
                usuarioDAO.salvar(usuario2);
                System.out.println("Usuário criado: " + usuario2);

                // Criação dos projetos
                Projeto projeto1 = new Projeto();
                projeto1.setTitulo("Aplicativo de Gestão de Tarefas");
                projeto1.setDescricao("App mobile para gerenciamento pessoal de tarefas e compromissos");
                projeto1.setUsuarioId(usuario1.getId());
                projeto1.setStatus("NAO_INICIADO");
                projetoDAO.salvar(projeto1);
                System.out.println("Projeto criado: " + projeto1);

                Projeto projeto2 = new Projeto();
                projeto2.setTitulo("Sistema de Notificações Push");
                projeto2.setDescricao("Serviço de envio de notificações para aplicativos móveis");
                projeto2.setUsuarioId(usuario2.getId());
                projeto2.setStatus("EM_ANDAMENTO");
                projetoDAO.salvar(projeto2);
                System.out.println("Projeto criado: " + projeto2);

                // Criação das tarefas
                Tarefa tarefa1 = new Tarefa();
                tarefa1.setTitulo("Design de interface do app");
                tarefa1.setDescricao("Criar wireframes e mockups para todas as telas");
                tarefa1.setProjetoId(projeto1.getId());
                tarefa1.setUsuarioResponsavelId(usuario2.getId());
                tarefa1.setPrioridade("ALTA");
                tarefa1.setDataConclusao(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
                tarefaDAO.salvar(tarefa1);
                System.out.println("Tarefa criada: " + tarefa1);

                Tarefa tarefa2 = new Tarefa();
                tarefa2.setTitulo("Implementar SDK de notificações");
                tarefa2.setDescricao("Integrar Firebase Cloud Messaging no sistema");
                tarefa2.setProjetoId(projeto2.getId());
                tarefa2.setUsuarioResponsavelId(usuario2.getId());
                tarefa2.setPrioridade("MEDIA");
                tarefa2.setDataConclusao(Timestamp.valueOf(LocalDateTime.now().plusDays(12)));
                tarefaDAO.salvar(tarefa2);
                System.out.println("Tarefa criada: " + tarefa2);

                // Criação das soluções
                Solucao solucao1 = new Solucao();
                solucao1.setTitulo("Interface em Flutter");
                solucao1.setDescricao("Design minimalista com tema claro/escuro seguindo Material Design");
                solucao1.setTarefaId(tarefa1.getId());
                solucao1.setUsuarioId(usuario2.getId());
                solucao1.setStatus("PENDENTE");
                solucaoDAO.salvar(solucao1);
                System.out.println("Solução criada: " + solucao1);

                Solucao solucao2 = new Solucao();
                solucao2.setTitulo("SDK multiplataforma com Firebase");
                solucao2.setDescricao("Wrapper unificado para iOS e Android usando Firebase");
                solucao2.setTarefaId(tarefa2.getId());
                solucao2.setUsuarioId(usuario1.getId());
                solucao2.setStatus("PENDENTE");
                solucaoDAO.salvar(solucao2);
                System.out.println("Solução criada: " + solucao2);

                // Criação das avaliações
                Avaliacao avaliacao1 = new Avaliacao();
                avaliacao1.setNota(5);
                avaliacao1.setComentario("Design excelente, atende todos os requisitos de usabilidade e acessibilidade");
                avaliacao1.setSolucaoId(solucao1.getId());
                avaliacao1.setUsuarioAvaliadorId(usuario1.getId());
                avaliacaoDAO.salvar(avaliacao1);
                System.out.println("Avaliação criada: " + avaliacao1);

                Avaliacao avaliacao2 = new Avaliacao();
                avaliacao2.setNota(4);
                avaliacao2.setComentario("Boa implementação, faltou apenas mais testes unitários");
                avaliacao2.setSolucaoId(solucao2.getId());
                avaliacao2.setUsuarioAvaliadorId(usuario2.getId());
                avaliacaoDAO.salvar(avaliacao2);
                System.out.println("Avaliação criada: " + avaliacao2);

                // Testes de leitura
                System.out.println("\n--- Testes de Leitura ---");
                Usuario foundUsuario = (Usuario) usuarioDAO.buscarPorId(usuario1.getId());
                System.out.println("Usuário encontrado por ID: " + foundUsuario);

                List<Projeto> projetos = projetoDAO.listarTodosLazyLoading();
                System.out.println("\nTodos os Projetos:");
                for (Projeto p : projetos) {
                    System.out.println("  " + p);
                }

                Solucao foundSolucao = (Solucao) solucaoDAO.buscarPorId(solucao1.getId());
                System.out.println("\nSolução encontrada por ID: " + foundSolucao);

                // Testes de atualização
                System.out.println("\n--- Testes de Atualização ---");
                foundUsuario.setNome("Pedro Almeida Silva");
                foundUsuario.setEmail("pedro.silva@company.com");
                usuarioDAO.atualizar(foundUsuario);
                Usuario usuarioAtualizado = (Usuario) usuarioDAO.buscarPorId(usuario1.getId());
                System.out.println("Usuário atualizado: " + usuarioAtualizado);

                // Testes de exclusão
                System.out.println("\n--- Testes de Deleção ---");
                avaliacaoDAO.excluir(avaliacao2.getId());
                System.out.println("Avaliação excluída");

                solucaoDAO.excluir(solucao2.getId());
                System.out.println("Solução excluída");

                tarefaDAO.excluir(tarefa2.getId());
                System.out.println("Tarefa excluída");

                projetoDAO.excluir(projeto2.getId());
                System.out.println("Projeto excluído");

            } catch (Exception e) {
                System.err.println("Erro durante os testes: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Erro na conexão com o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
