package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Avaliacao;

public class AvaliacaoDAO implements BaseDAO<Avaliacao> {

    private Connection connection;

    public AvaliacaoDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS avaliacoes (
                id VARCHAR(36) PRIMARY KEY,
                nota INT NOT NULL CHECK (nota >= 0 AND nota <= 5),
                comentario TEXT,
                data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                solucao_id VARCHAR(36),
                usuario_avaliador_id VARCHAR(36),
                FOREIGN KEY (solucao_id) REFERENCES solucoes(id),
                FOREIGN KEY (usuario_avaliador_id) REFERENCES usuario(id)
            )
        """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacoes (id, nota, comentario, solucao_id, usuario_avaliador_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, avaliacao.getId());
            pstm.setInt(2, avaliacao.getNota());
            pstm.setString(3, avaliacao.getComentario());
            pstm.setString(4, avaliacao.getSolucaoId());
            pstm.setString(5, avaliacao.getUsuarioAvaliadorId());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar avaliação: " + e.getMessage());
        }
    }

    public List<Avaliacao> buscarPorSolucao(String solucaoId) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes WHERE solucao_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucaoId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    avaliacoes.add(criarAvaliacao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar avaliações por solução: " + e.getMessage());
        }
        return avaliacoes;
    }

    public List<Avaliacao> buscarPorAvaliador(String usuarioId) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes WHERE usuario_avaliador_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    avaliacoes.add(criarAvaliacao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar avaliações por avaliador: " + e.getMessage());
        }
        return avaliacoes;
    }

    @Override
    public Object buscarPorId(String id) {
        String sql = "SELECT * FROM avaliacoes WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarAvaliacao(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar avaliação: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Avaliacao> listarTodosLazyLoading() {
        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                avaliacoes.add(criarAvaliacao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar avaliações: " + e.getMessage());
        }
        return avaliacoes;
    }

    @Override
    public void atualizar(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacoes SET nota = ?, comentario = ? WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, avaliacao.getNota());
            pstm.setString(2, avaliacao.getComentario());
            pstm.setString(3, avaliacao.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou: avaliação não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar avaliação: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM avaliacoes WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Exclusão falhou: avaliação não encontrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir avaliação: " + e.getMessage());
        }
    }

    private Avaliacao criarAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(rs.getString("id"));
        avaliacao.setNota(rs.getInt("nota"));
        avaliacao.setComentario(rs.getString("comentario"));
        avaliacao.setSolucaoId(rs.getString("solucao_id"));
        avaliacao.setUsuarioAvaliadorId(rs.getString("usuario_avaliador_id"));
        avaliacao.setDataAvaliacao(rs.getTimestamp("data_avaliacao"));
        return avaliacao;
    }

    public double calcularMediaAvaliacoesSolucao(String solucaoId) {
        String sql = "SELECT AVG(nota) as media FROM avaliacoes WHERE solucao_id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucaoId);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("media");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular média das avaliações: " + e.getMessage());
        }
        return 0.0;
    }

    public Map<String, Double> obterEstatisticasGerais() {
        Map<String, Double> estatisticas = new HashMap<>();
        String sql = """
            SELECT 
                AVG(nota) as media_geral,
                MIN(nota) as nota_minima,
                MAX(nota) as nota_maxima,
                COUNT(*) as total_avaliacoes
            FROM avaliacoes
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            if (rs.next()) {
                estatisticas.put("media_geral", rs.getDouble("media_geral"));
                estatisticas.put("nota_minima", rs.getDouble("nota_minima"));
                estatisticas.put("nota_maxima", rs.getDouble("nota_maxima"));
                estatisticas.put("total_avaliacoes", rs.getDouble("total_avaliacoes"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter estatísticas gerais: " + e.getMessage());
        }
        return estatisticas;
    }

    public List<Map<String, Object>> obterRankingSolucoes(int limite) {
        List<Map<String, Object>> ranking = new ArrayList<>();
        String sql = """
            SELECT 
                s.id as solucao_id,
                s.titulo as solucao_titulo,
                u.nome as autor,
                COUNT(a.id) as total_avaliacoes,
                AVG(a.nota) as media_notas
            FROM solucoes s
            INNER JOIN usuario u ON s.usuario_id = u.id
            INNER JOIN avaliacoes a ON s.id = a.solucao_id
            GROUP BY s.id, s.titulo, u.nome
            HAVING COUNT(a.id) >= 3
            ORDER BY media_notas DESC
            LIMIT ?
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, limite);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("solucao_id", rs.getString("solucao_id"));
                    item.put("solucao_titulo", rs.getString("solucao_titulo"));
                    item.put("autor", rs.getString("autor"));
                    item.put("total_avaliacoes", rs.getInt("total_avaliacoes"));
                    item.put("media_notas", rs.getDouble("media_notas"));
                    ranking.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter ranking de soluções: " + e.getMessage());
        }
        return ranking;
    }

    public Map<String, Long> obterDistribuicaoNotas() {
        Map<String, Long> distribuicao = new HashMap<>();
        String sql = """
            SELECT 
                nota,
                COUNT(*) as quantidade
            FROM avaliacoes
            GROUP BY nota
            ORDER BY nota
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                distribuicao.put(
                    String.valueOf(rs.getInt("nota")),
                    rs.getLong("quantidade")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter distribuição de notas: " + e.getMessage());
        }
        return distribuicao;
    }

    public double calcularMediaPorPeriodo(String solucaoId, java.sql.Date dataInicio, java.sql.Date dataFim) {
        String sql = """
            SELECT AVG(nota) as media 
            FROM avaliacoes 
            WHERE solucao_id = ? 
            AND data_avaliacao BETWEEN ? AND ?
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, solucaoId);
            pstm.setDate(2, dataInicio);
            pstm.setDate(3, dataFim);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("media");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular média por período: " + e.getMessage());
        }
        return 0.0;
    }
}
