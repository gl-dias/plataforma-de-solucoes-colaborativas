import bd.ConnectionFactory;
import dao.*;
import modelo.*;

import java.sql.Connection;
import java.util.ArrayList;
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
                Usuario usuario1 = new Usuario(userId1, "Alice Silva", "alice@example.com", "senha123", perfil1);
                usuarioDAO.salvar(usuario1);
                System.out.println("Usuário criado: " + usuario1);

                String userId2 = UUID.randomUUID().toString();
                Usuario usuario2 = new Usuario(userId2, "Bob Santos", "bob@example.com", "senha456", perfil2);
                usuarioDAO.salvar(usuario2);
                System.out.println("Usuário criado: " + usuario2);

                // Criação dos projetos
                String projetoId1 = UUID.randomUUID().toString();
                Projeto projeto1 = new Projeto(projetoId1, "Plataforma de Colaboração Online", "Desenvolver uma plataforma para gerenciamento de projetos.", "Ativo", usuario1);
                projetoDAO.salvar(projeto1);
                System.out.println("Projeto criado: " + projeto1);

                String projetoId2 = UUID.randomUUID().toString();
                Projeto projeto2 = new Projeto(projetoId2, "Aplicativo de Lista de Tarefas", "Criar um aplicativo simples para organização pessoal.", "Em andamento", usuario2);
                projetoDAO.salvar(projeto2);
                System.out.println("Projeto criado: " + projeto2);

                // Criação das tarefas
                String tarefaId1 = UUID.randomUUID().toString();
                Tarefa tarefa1 = new Tarefa(tarefaId1, "Implementar autenticação de usuário", "Criar sistema de login e registro.", "Aberta", 100, projeto1);
                tarefaDAO.salvar(tarefa1);
                System.out.println("Tarefa criada: " + tarefa1);

                String tarefaId2 = UUID.randomUUID().toString();
                Tarefa tarefa2 = new Tarefa(tarefaId2, "Desenhar interface do usuário", "Criar wireframes e mockups para o aplicativo.", "Aberta", 150, projeto2);
                tarefaDAO.salvar(tarefa2);
                System.out.println("Tarefa criada: " + tarefa2);

                // Criação das soluções
                String solucaoId1 = UUID.randomUUID().toString();
                Solucao solucao1 = new Solucao(solucaoId1, "Código da autenticação com Spring Security.", usuario1, tarefa1, "Pendente");
                solucaoDAO.salvar(solucao1);
                System.out.println("Solução criada: " + solucao1);

                String solucaoId2 = UUID.randomUUID().toString();
                Solucao solucao2 = new Solucao(solucaoId2, "Mockups de alta fidelidade para o app.", usuario2, tarefa2, "Pendente");
                solucaoDAO.salvar(solucao2);
                System.out.println("Solução criada: " + solucao2);

                // Criação das avaliações
                String avaliacaoId1 = UUID.randomUUID().toString();
                Avaliacao avaliacao1 = new Avaliacao(avaliacaoId1, 4, "Ótima implementação, segura e bem documentada.", usuario2, solucao1);
                avaliacaoDAO.salvar(avaliacao1);
                System.out.println("Avaliação criada: " + avaliacao1);

                String avaliacaoId2 = UUID.randomUUID().toString();
                Avaliacao avaliacao2 = new Avaliacao(avaliacaoId2, 5, "Design inovador e intuitivo. Excelente trabalho!", usuario1, solucao2);
                avaliacaoDAO.salvar(avaliacao2);
                System.out.println("Avaliação criada: " + avaliacao2);

                // Testes de leitura
                System.out.println("\n--- Testes de Leitura ---");
                Object foundUsuario = usuarioDAO.buscarPorId(userId1);
                System.out.println("Usuário encontrado por ID: " + foundUsuario);

                ArrayList<Object> projetos = projetoDAO.listarTodosLazyLoading();
                System.out.println("\nTodos os Projetos:");
                for (Object obj : projetos) {
                    if (obj instanceof Projeto) {
                        Projeto p = (Projeto) obj;
                        System.out.println("  " + p);
                    }
                }

                Object foundSolucao = solucaoDAO.buscarPorId(solucaoId1);
                System.out.println("\nSolução encontrada por ID: " + foundSolucao);

                // Testes de atualização
                System.out.println("\n--- Testes de Atualização ---");
                if (foundUsuario instanceof Usuario) {
                    Usuario u = (Usuario) foundUsuario;
                    u.setNome("Alice Pereira");
                    u.setEmail("alice.pereira@example.com");
                    usuarioDAO.atualizar(u);
                    System.out.println("Usuário atualizado: " + usuarioDAO.buscarPorId(userId1));
                }

                // Testes de exclusão
                System.out.println("\n--- Testes de Deleção ---");
                avaliacaoDAO.excluir(avaliacaoId1);
                System.out.println("Avaliação excluída");

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
