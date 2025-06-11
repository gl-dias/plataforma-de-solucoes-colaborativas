package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Projeto;
import modelo.Usuario;

public class ProjetoDAO implements BaseDAO {

    private Connection connection;

    public ProjetoDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS projetos (
                id VARCHAR(36) PRIMARY KEY,
                titulo VARCHAR(100) NOT NULL,
                descricao TEXT,
                data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                data_conclusao TIMESTAMP,
                usuario_id VARCHAR(36),
                status VARCHAR(20) DEFAULT 'EM_ANDAMENTO',
                FOREIGN KEY (usuario_id) REFERENCES usuario(id)
            )
        """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Projeto)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Projeto");
        }

        Projeto projeto = (Projeto) objeto;
        String sql = "INSERT INTO projetos (id, titulo, descricao, usuario_id, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, projeto.getId());
            pstm.setString(2, projeto.getTitulo());
            pstm.setString(3, projeto.getDescricao());
            pstm.setString(4, projeto.getUsuarioId());
            pstm.setString(5, projeto.getStatus());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar projeto: " + e.getMessage());
        }
    }

    public List<Projeto> buscarPorUsuario(String usuarioId) {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projetos WHERE usuario_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    projetos.add(criarProjeto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projetos por usuário: " + e.getMessage());
        }
        return projetos;
    }

    @Override
    public Object buscarPorId(String id) {
        String sql = "SELECT * FROM projetos WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarProjeto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projeto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projetos";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                projetos.add(criarProjeto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar projetos: " + e.getMessage());
        }
        return projetos;
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Projeto)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Projeto");
        }

        Projeto projeto = (Projeto) objeto;
        String sql = "UPDATE projetos SET titulo = ?, descricao = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, projeto.getTitulo());
            pstm.setString(2, projeto.getDescricao());
            pstm.setString(3, projeto.getStatus());
            pstm.setString(4, projeto.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou: projeto não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar projeto: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        // Primeiro, exclui todas as tarefas relacionadas
        excluirTarefasRelacionadas(id);

        String sql = "DELETE FROM projetos WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Exclusão falhou: projeto não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir projeto: " + e.getMessage());
        }
    }

    private void excluirTarefasRelacionadas(String projetoId) {
        TarefaDAO tarefaDAO = new TarefaDAO(connection);
        List<Object> tarefas = tarefaDAO.buscarPorProjeto(projetoId);

        for (Object obj : tarefas) {
            tarefaDAO.excluir(((Tarefa)obj).getId());
        }
    }

    private Projeto criarProjeto(ResultSet rs) throws SQLException {
        Projeto projeto = new Projeto();
        projeto.setId(rs.getString("id"));
        projeto.setTitulo(rs.getString("titulo"));
        projeto.setDescricao(rs.getString("descricao"));
        projeto.setUsuarioId(rs.getString("usuario_id"));
        projeto.setStatus(rs.getString("status"));
        projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
        projeto.setDataConclusao(rs.getTimestamp("data_conclusao"));
        return projeto;
    }

    public void concluirProjeto(String id) {
        String sql = "UPDATE projetos SET status = 'CONCLUIDO', data_conclusao = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Conclusão falhou: projeto não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao concluir projeto: " + e.getMessage());
        }
    }

    public List<Projeto> buscarProjetosAtivos() {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projetos WHERE status = 'EM_ANDAMENTO'";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                projetos.add(criarProjeto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projetos ativos: " + e.getMessage());
        }
        return projetos;
    }

    public List<Projeto> buscarProjetosPorPeriodo(java.sql.Date dataInicio, java.sql.Date dataFim) {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projetos WHERE data_criacao BETWEEN ? AND ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setDate(1, dataInicio);
            pstm.setDate(2, dataFim);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    projetos.add(criarProjeto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projetos por período: " + e.getMessage());
        }
        return projetos;
    }

    public Map<String, Long> obterEstatisticasProjetos() {
        Map<String, Long> estatisticas = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN status = 'EM_ANDAMENTO' THEN 1 ELSE 0 END) as em_andamento,
                SUM(CASE WHEN status = 'CONCLUIDO' THEN 1 ELSE 0 END) as concluidos
            FROM projetos
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            if (rs.next()) {
                estatisticas.put("total", rs.getLong("total"));
                estatisticas.put("em_andamento", rs.getLong("em_andamento"));
                estatisticas.put("concluidos", rs.getLong("concluidos"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter estatísticas dos projetos: " + e.getMessage());
        }
        return estatisticas;
    }

    public double calcularProgressoProjeto(String projetoId) {
        String sql = """
            SELECT 
                COUNT(*) as total_tarefas,
                SUM(CASE WHEN status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas
            FROM tarefas 
            WHERE projeto_id = ?
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, projetoId);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    long totalTarefas = rs.getLong("total_tarefas");
                    long tarefasConcluidas = rs.getLong("tarefas_concluidas");

                    if (totalTarefas > 0) {
                        return (double) tarefasConcluidas / totalTarefas * 100;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular progresso do projeto: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Usuario> buscarEquipeProjeto(String projetoId) {
        List<Usuario> equipe = new ArrayList<>();
        String sql = """
            SELECT DISTINCT u.* 
            FROM usuario u 
            INNER JOIN tarefas t ON u.id = t.usuario_responsavel_id 
            WHERE t.projeto_id = ?
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, projetoId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Usuario membro = new Usuario();
                    membro.setId(rs.getString("id"));
                    membro.setNome(rs.getString("nome"));
                    membro.setEmail(rs.getString("email"));
                    equipe.add(membro);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar equipe do projeto: " + e.getMessage());
        }
        return equipe;
    }
}
