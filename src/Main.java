import bd.ConnectionFactory;
import dao.*;
import modelo.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = new ConnectionFactory().recuperaConexao()) {
            PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO(connection);
            UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
            ProjetoDAO projetoDAO = new ProjetoDAO(connection);
            TarefaDAO tarefaDAO = new TarefaDAO(connection);
            SolucaoDAO solucaoDAO = new SolucaoDAO(connection);
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(connection);

            try {
                perfilUsuarioDAO.criarTabela();
                usuarioDAO.criarTabela();
                projetoDAO.criarTabela();
                tarefaDAO.criarTabela();
                solucaoDAO.criarTabela();
                avaliacaoDAO.criarTabela();

                System.out.println("\n--- Testes de Criação ---");

                // Criação dos perfis de usuário
                String perfilId1 = UUID.randomUUID().toString();
                PerfilUsuario perfil1 = new PerfilUsuario(perfilId1, "Amante de tecnologia e inovação.", "http://example.com/foto1.jpg");
                perfil1.adicionarHabilidade("Java");
                perfil1.adicionarHabilidade("SQL");
                perfilUsuarioDAO.salvar(perfil1);
                System.out.println("Perfil criado: " + perfil1);

                String perfilId2 = UUID.randomUUID().toString();
                PerfilUsuario perfil2 = new PerfilUsuario(perfilId2, "Designer gráfico e entusiasta de UI/UX.", "http://example.com/foto2.jpg");
                perfil2.adicionarHabilidade("Photoshop");
                perfil2.adicionarHabilidade("Illustrator");
                perfilUsuarioDAO.salvar(perfil2);
                System.out.println("Perfil criado: " + perfil2);

                // Criação dos usuários
                String userId1 = UUID.randomUUID().toString();
                Usuario usuario1 = new Usuario(userId1, "Alice Silva", "alice@example.com", "senha123");
                usuario1.setPerfil(perfil1);
                usuarioDAO.salvar(usuario1);
                System.out.println("Usuário criado: " + usuario1);

                String userId2 = UUID.randomUUID().toString();
                Usuario usuario2 = new Usuario(userId2, "Bob Santos", "bob@example.com", "senha456");
                usuario2.setPerfil(perfil2);
                usuarioDAO.salvar(usuario2);
                System.out.println("Usuário criado: " + usuario2);

                // Criação dos projetos
                String projetoId1 = UUID.randomUUID().toString();
                Projeto projeto1 = new Projeto(projetoId1, "Plataforma de Colaboração Online", "Desenvolver uma plataforma para gerenciamento de projetos.", userId1);
                projeto1.setStatus("EM_ANDAMENTO");
                projetoDAO.salvar(projeto1);
                System.out.println("Projeto criado: " + projeto1);

                String projetoId2 = UUID.randomUUID().toString();
                Projeto projeto2 = new Projeto(projetoId2, "Aplicativo de Lista de Tarefas", "Criar um aplicativo simples para organização pessoal.", userId2);
                projeto2.setStatus("EM_ANDAMENTO");
                projetoDAO.salvar(projeto2);
                System.out.println("Projeto criado: " + projeto2);

                // Criação das tarefas
                String tarefaId1 = UUID.randomUUID().toString();
                Tarefa tarefa1 = new Tarefa(tarefaId1, "Implementar autenticação de usuário", "Criar sistema de login e registro.", projetoId1, userId1);
                tarefa1.setPrioridade("ALTA");
                tarefaDAO.salvar(tarefa1);
                System.out.println("Tarefa criada: " + tarefa1);

                String tarefaId2 = UUID.randomUUID().toString();
                Tarefa tarefa2 = new Tarefa(tarefaId2, "Desenhar interface do usuário", "Criar wireframes e mockups para o aplicativo.", projetoId2, userId2);
                tarefa2.setPrioridade("MEDIA");
                tarefaDAO.salvar(tarefa2);
                System.out.println("Tarefa criada: " + tarefa2);

                // Criação das soluções
                String solucaoId1 = UUID.randomUUID().toString();
                Solucao solucao1 = new Solucao(solucaoId1, "Código da autenticação com Spring Security.", "Implementação completa do módulo de autenticação.", tarefaId1, userId1);
                solucao1.setStatus("PENDENTE");
                solucaoDAO.salvar(solucao1);
                System.out.println("Solução criada: " + solucao1);

                String solucaoId2 = UUID.randomUUID().toString();
                Solucao solucao2 = new Solucao(solucaoId2, "Mockups de alta fidelidade para o app.", "Design completo das telas principais.", tarefaId2, userId2);
                solucao2.setStatus("PENDENTE");
                solucaoDAO.salvar(solucao2);
                System.out.println("Solução criada: " + solucao2);

                // Criação das avaliações
                String avaliacaoId1 = UUID.randomUUID().toString();
                Avaliacao avaliacao1 = new Avaliacao(avaliacaoId1, 4, "Ótima implementação, segura e bem documentada.", solucaoId1, userId2);
                avaliacaoDAO.salvar(avaliacao1);
                System.out.println("Avaliação criada: " + avaliacao1);

                String avaliacaoId2 = UUID.randomUUID().toString();
                Avaliacao avaliacao2 = new Avaliacao(avaliacaoId2, 5, "Design inovador e intuitivo. Excelente trabalho!", solucaoId2, userId1);
                avaliacaoDAO.salvar(avaliacao2);
                System.out.println("Avaliação criada: " + avaliacao2);

                // Testes de leitura
                System.out.println("\n--- Testes de Leitura ---");
                Usuario foundUsuario = (Usuario) usuarioDAO.buscarPorId(userId1);
                System.out.println("Usuário encontrado por ID: " + foundUsuario);

                List<Projeto> projetos = projetoDAO.listarTodosLazyLoading();
                System.out.println("\nTodos os Projetos:");
                for (Projeto p : projetos) {
                    System.out.println("  " + p);
                }

                Solucao foundSolucao = (Solucao) solucaoDAO.buscarPorId(solucaoId1);
                System.out.println("\nSolução encontrada por ID: " + foundSolucao);

                // Testes de atualização
                System.out.println("\n--- Testes de Atualização ---");
                foundUsuario.setNome("Alice Pereira");
                foundUsuario.setEmail("alice.pereira@example.com");
                usuarioDAO.atualizar(foundUsuario);
                Usuario usuarioAtualizado = (Usuario) usuarioDAO.buscarPorId(userId1);
                System.out.println("Usuário atualizado: " + usuarioAtualizado);

                // Testes de exclusão
                System.out.println("\n--- Testes de Deleção ---");
                avaliacaoDAO.excluir(avaliacaoId1);
                System.out.println("Avaliação excluída");

                solucaoDAO.excluir(solucaoId2);
                System.out.println("Solução excluída");

                tarefaDAO.excluir(tarefaId2);
                System.out.println("Tarefa excluída");

                projetoDAO.excluir(projetoId2);
                System.out.println("Projeto excluído");

            } catch (Exception e) {
                System.out.println("Erro durante a execução dos testes: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Erro ao obter conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
