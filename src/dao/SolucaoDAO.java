package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Solucao;

public class SolucaoDAO implements BaseDAO<Solucao> {

    private Connection connection;

    public SolucaoDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS solucoes (
                id VARCHAR(36) PRIMARY KEY,
                titulo VARCHAR(100) NOT NULL,
                descricao TEXT,
                data_submissao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                tarefa_id VARCHAR(36),
                usuario_id VARCHAR(36),
                status VARCHAR(20) DEFAULT 'PENDENTE',
                FOREIGN KEY (tarefa_id) REFERENCES tarefas(id),
                FOREIGN KEY (usuario_id) REFERENCES usuario(id)
            )
        """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Solucao solucao) {
        String sql = "INSERT INTO solucoes (id, titulo, descricao, tarefa_id, usuario_id, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucao.getId());
            pstm.setString(2, solucao.getTitulo());
            pstm.setString(3, solucao.getDescricao());
            pstm.setString(4, solucao.getTarefaId());
            pstm.setString(5, solucao.getUsuarioId());
            pstm.setString(6, solucao.getStatus());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar solução: " + e.getMessage());
        }
    }

    public List<Solucao> buscarPorTarefa(String tarefaId) {
        List<Solucao> solucoes = new ArrayList<>();
        String sql = "SELECT * FROM solucoes WHERE tarefa_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, tarefaId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    solucoes.add(criarSolucao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar soluções por tarefa: " + e.getMessage());
        }
        return solucoes;
    }

    public List<Solucao> buscarPorUsuario(String usuarioId) {
        List<Solucao> solucoes = new ArrayList<>();
        String sql = "SELECT * FROM solucoes WHERE usuario_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    solucoes.add(criarSolucao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar soluções por usuário: " + e.getMessage());
        }
        return solucoes;
    }

    @Override
    public Object buscarPorId(String id) {
        String sql = "SELECT * FROM solucoes WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarSolucao(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar solução: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Solucao> listarTodosLazyLoading() {
        ArrayList<Solucao> solucoes = new ArrayList<>();
        String sql = "SELECT * FROM solucoes";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                solucoes.add(criarSolucao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar soluções: " + e.getMessage());
        }
        return solucoes;
    }

    @Override
    public void atualizar(Solucao solucao) {
        String sql = "UPDATE solucoes SET titulo = ?, descricao = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucao.getTitulo());
            pstm.setString(2, solucao.getDescricao());
            pstm.setString(3, solucao.getStatus());
            pstm.setString(4, solucao.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou: solução não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar solução: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        // Primeiro, verifica se existem avaliações relacionadas
        excluirAvaliacoesRelacionadas(id);

        String sql = "DELETE FROM solucoes WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Exclusão falhou: solução não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir solução: " + e.getMessage());
        }
    }

    private void excluirAvaliacoesRelacionadas(String solucaoId) {
        String sql = "DELETE FROM avaliacoes WHERE solucao_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucaoId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir avaliações relacionadas: " + e.getMessage());
        }
    }

    private Solucao criarSolucao(ResultSet rs) throws SQLException {
        Solucao solucao = new Solucao();
        solucao.setId(rs.getString("id"));
        solucao.setTitulo(rs.getString("titulo"));
        solucao.setDescricao(rs.getString("descricao"));
        solucao.setTarefaId(rs.getString("tarefa_id"));
        solucao.setUsuarioId(rs.getString("usuario_id"));
        solucao.setStatus(rs.getString("status"));
        solucao.setDataSubmissao(rs.getTimestamp("data_submissao"));
        return solucao;
    }

    public void atualizarStatus(String id, String novoStatus) {
        String sql = "UPDATE solucoes SET status = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, novoStatus);
            pstm.setString(2, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização de status falhou: solução não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da solução: " + e.getMessage());
        }
    }

    public List<Solucao> buscarPorStatus(String status) {
        List<Solucao> solucoes = new ArrayList<>();
        String sql = "SELECT * FROM solucoes WHERE status = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, status);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    solucoes.add(criarSolucao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar soluções por status: " + e.getMessage());
        }
        return solucoes;
    }

    public long contarSolucoesPorUsuario(String usuarioId) {
        String sql = "SELECT COUNT(*) FROM solucoes WHERE usuario_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar soluções do usuário: " + e.getMessage());
        }
        return 0;
    }

    public List<Solucao> buscarSolucoesRecentes(int limite) {
        List<Solucao> solucoes = new ArrayList<>();
        String sql = "SELECT * FROM solucoes ORDER BY data_submissao DESC LIMIT ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, limite);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    solucoes.add(criarSolucao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar soluções recentes: " + e.getMessage());
        }
        return solucoes;
    }

    public List<Solucao> buscarSolucoesPopulares(int limite) {
        List<Solucao> solucoes = new ArrayList<>();
        String sql = """
            SELECT s.*, AVG(a.nota) as media_avaliacoes 
            FROM solucoes s 
            LEFT JOIN avaliacoes a ON s.id = a.solucao_id 
            GROUP BY s.id 
            ORDER BY media_avaliacoes DESC 
            LIMIT ?
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, limite);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    solucoes.add(criarSolucao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar soluções populares: " + e.getMessage());
        }
        return solucoes;
    }
}
