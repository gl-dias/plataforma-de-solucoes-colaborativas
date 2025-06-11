package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Tarefa;

public class TarefaDAO implements BaseDAO {

    private Connection connection;

    public TarefaDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS tarefas (
                id VARCHAR(36) PRIMARY KEY,
                titulo VARCHAR(100) NOT NULL,
                descricao TEXT,
                status VARCHAR(20) DEFAULT 'PENDENTE',
                data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                data_conclusao TIMESTAMP,
                projeto_id VARCHAR(36),
                usuario_responsavel_id VARCHAR(36),
                prioridade VARCHAR(20) DEFAULT 'MEDIA',
                FOREIGN KEY (projeto_id) REFERENCES projetos(id),
                FOREIGN KEY (usuario_responsavel_id) REFERENCES usuario(id)
            )
        """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Tarefa)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Tarefa");
        }

        Tarefa tarefa = (Tarefa) objeto;
        String sql = "INSERT INTO tarefas (id, titulo, descricao, status, projeto_id, usuario_responsavel_id, prioridade) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, tarefa.getId());
            pstm.setString(2, tarefa.getTitulo());
            pstm.setString(3, tarefa.getDescricao());
            pstm.setString(4, tarefa.getStatus());
            pstm.setString(5, tarefa.getProjetoId());
            pstm.setString(6, tarefa.getUsuarioResponsavelId());
            pstm.setString(7, tarefa.getPrioridade());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tarefa: " + e.getMessage());
        }
    }

    public List<Tarefa> buscarPorProjeto(String projetoId) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE projeto_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, projetoId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(criarTarefa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas por projeto: " + e.getMessage());
        }
        return tarefas;
    }

    public List<Tarefa> buscarPorResponsavel(String usuarioId) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE usuario_responsavel_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(criarTarefa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas por responsável: " + e.getMessage());
        }
        return tarefas;
    }

    @Override
    public Object buscarPorId(String id) {
        String sql = "SELECT * FROM tarefas WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarTarefa(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                tarefas.add(criarTarefa(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas: " + e.getMessage());
        }
        return tarefas;
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Tarefa)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo Tarefa");
        }

        Tarefa tarefa = (Tarefa) objeto;
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, status = ?, usuario_responsavel_id = ?, prioridade = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, tarefa.getTitulo());
            pstm.setString(2, tarefa.getDescricao());
            pstm.setString(3, tarefa.getStatus());
            pstm.setString(4, tarefa.getUsuarioResponsavelId());
            pstm.setString(5, tarefa.getPrioridade());
            pstm.setString(6, tarefa.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou: tarefa não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        // Primeiro, exclui todas as soluções relacionadas
        excluirSolucoesRelacionadas(id);

        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Exclusão falhou: tarefa não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir tarefa: " + e.getMessage());
        }
    }

    private void excluirSolucoesRelacionadas(String tarefaId) {
        SolucaoDAO solucaoDAO = new SolucaoDAO(connection);
        List<Solucao> solucoes = solucaoDAO.buscarPorTarefa(tarefaId);

        for (Solucao solucao : solucoes) {
            solucaoDAO.excluir(solucao.getId());
        }
    }

    private Tarefa criarTarefa(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(rs.getString("id"));
        tarefa.setTitulo(rs.getString("titulo"));
        tarefa.setDescricao(rs.getString("descricao"));
        tarefa.setStatus(rs.getString("status"));
        tarefa.setProjetoId(rs.getString("projeto_id"));
        tarefa.setUsuarioResponsavelId(rs.getString("usuario_responsavel_id"));
        tarefa.setPrioridade(rs.getString("prioridade"));
        tarefa.setDataCriacao(rs.getTimestamp("data_criacao"));
        tarefa.setDataConclusao(rs.getTimestamp("data_conclusao"));
        return tarefa;
    }

    public void concluirTarefa(String id) {
        String sql = "UPDATE tarefas SET status = 'CONCLUIDA', data_conclusao = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Conclusão falhou: tarefa não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao concluir tarefa: " + e.getMessage());
        }
    }

    public List<Tarefa> buscarTarefasPendentes() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE status = 'PENDENTE'";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                tarefas.add(criarTarefa(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas pendentes: " + e.getMessage());
        }
        return tarefas;
    }

    public List<Tarefa> buscarPorPrioridade(String prioridade) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE prioridade = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, prioridade);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(criarTarefa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas por prioridade: " + e.getMessage());
        }
        return tarefas;
    }

    public List<Tarefa> buscarTarefasEmAtraso() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = """
            SELECT * FROM tarefas 
            WHERE status != 'CONCLUIDA' 
            AND data_conclusao < CURRENT_TIMESTAMP
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                tarefas.add(criarTarefa(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas em atraso: " + e.getMessage());
        }
        return tarefas;
    }

    public Map<String, Long> obterEstatisticasPorPrioridade() {
        Map<String, Long> estatisticas = new HashMap<>();
        String sql = """
            SELECT 
                prioridade,
                COUNT(*) as quantidade
            FROM tarefas
            GROUP BY prioridade
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                estatisticas.put(
                    rs.getString("prioridade"),
                    rs.getLong("quantidade")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter estatísticas por prioridade: " + e.getMessage());
        }
        return estatisticas;
    }

    public Map<String, Long> obterEstatisticasPorStatus() {
        Map<String, Long> estatisticas = new HashMap<>();
        String sql = """
            SELECT 
                status,
                COUNT(*) as quantidade
            FROM tarefas
            GROUP BY status
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                estatisticas.put(
                    rs.getString("status"),
                    rs.getLong("quantidade")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter estatísticas por status: " + e.getMessage());
        }
        return estatisticas;
    }

    public List<Map<String, Object>> obterDesempenhoUsuarios() {
        List<Map<String, Object>> desempenho = new ArrayList<>();
        String sql = """
            SELECT 
                u.id as usuario_id,
                u.nome as usuario_nome,
                COUNT(t.id) as total_tarefas,
                SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas,
                AVG(CASE WHEN t.status = 'CONCLUIDA' 
                    THEN TIMESTAMPDIFF(DAY, t.data_criacao, t.data_conclusao)
                    ELSE NULL END) as media_dias_conclusao
            FROM usuario u
            LEFT JOIN tarefas t ON u.id = t.usuario_responsavel_id
            WHERE u.ativo = true
            GROUP BY u.id, u.nome
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("usuario_id", rs.getString("usuario_id"));
                usuario.put("usuario_nome", rs.getString("usuario_nome"));
                usuario.put("total_tarefas", rs.getLong("total_tarefas"));
                usuario.put("tarefas_concluidas", rs.getLong("tarefas_concluidas"));
                usuario.put("media_dias_conclusao", rs.getDouble("media_dias_conclusao"));
                desempenho.add(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter desempenho dos usuários: " + e.getMessage());
        }
        return desempenho;
    }

    public void atualizarPrioridade(String id, String novaPrioridade) {
        String sql = "UPDATE tarefas SET prioridade = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, novaPrioridade);
            pstm.setString(2, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização de prioridade falhou: tarefa não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar prioridade da tarefa: " + e.getMessage());
        }
    }

    public List<Tarefa> buscarProximasEntregas(int dias) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = """
            SELECT * FROM tarefas 
            WHERE status != 'CONCLUIDA' 
            AND data_conclusao <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? DAY)
            ORDER BY data_conclusao ASC
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, dias);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(criarTarefa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar próximas entregas: " + e.getMessage());
        }
        return tarefas;
    }
}
