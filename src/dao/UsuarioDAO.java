package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Usuario;
import modelo.PerfilUsuario;

public class UsuarioDAO implements BaseDAO<Usuario> {

    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuario (
                id VARCHAR(36) PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                senha_criptografada VARCHAR(100) NOT NULL,
                data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                ativo BOOLEAN DEFAULT true
            )
        """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (id, nome, email, senha_criptografada) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuario.getId());
            pstm.setString(2, usuario.getNome());
            pstm.setString(3, usuario.getEmail());
            pstm.setString(4, usuario.getSenhaCriptografada());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, email);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Object buscarPorId(String id) {
        String sql = "SELECT * FROM usuario WHERE id = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Usuario> listarTodosLazyLoading() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                usuarios.add(criarUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ? WHERE id = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getEmail());
            pstm.setString(3, usuario.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou: usuário não encontrado ou inativo.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    public void atualizarSenha(String id, String novaSenhaCriptografada) {
        String sql = "UPDATE usuario SET senha_criptografada = ? WHERE id = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, novaSenhaCriptografada);
            pstm.setString(2, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização de senha falhou: usuário não encontrado ou inativo.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar senha: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        // Soft delete - apenas marca o usuário como inativo
        String sql = "UPDATE usuario SET ativo = false WHERE id = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Exclusão falhou: usuário não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    private Usuario criarUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getString("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenhaCriptografada(rs.getString("senha_criptografada"));
        return usuario;
    }

    public boolean autenticar(String email, String senhaCriptografada) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ? AND senha_criptografada = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, email);
            pstm.setString(2, senhaCriptografada);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage());
        }
        return false;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, email);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar email: " + e.getMessage());
        }
        return false;
    }

    public Usuario buscarPorEmailESenha(String email, String senhaCriptografada) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha_criptografada = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, email);
            pstm.setString(2, senhaCriptografada);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return criarUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na autenticação: " + e.getMessage());
        }
        return null;
    }

    public boolean redefinirSenha(String email, String novaSenhaCriptografada) {
        String sql = "UPDATE usuario SET senha_criptografada = ? WHERE email = ? AND ativo = true";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, novaSenhaCriptografada);
            pstm.setString(2, email);

            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao redefinir senha: " + e.getMessage());
        }
    }

    public List<Usuario> buscarPorHabilidade(String habilidade) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.* 
            FROM usuario u 
            INNER JOIN perfil_usuario pu ON u.id = pu.usuario_id 
            WHERE pu.habilidades LIKE ? AND u.ativo = true
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, "%" + habilidade + "%");

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(criarUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por habilidade: " + e.getMessage());
        }
        return usuarios;
    }

    public Map<String, Object> obterEstatisticasUsuario(String usuarioId) {
        Map<String, Object> estatisticas = new HashMap<>();
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM projetos WHERE usuario_id = ?) as total_projetos,
                (SELECT COUNT(*) FROM tarefas WHERE usuario_responsavel_id = ?) as total_tarefas,
                (SELECT COUNT(*) FROM solucoes WHERE usuario_id = ?) as total_solucoes,
                (SELECT COUNT(*) FROM avaliacoes WHERE usuario_avaliador_id = ?) as total_avaliacoes,
                (SELECT AVG(nota) FROM avaliacoes a 
                 INNER JOIN solucoes s ON a.solucao_id = s.id 
                 WHERE s.usuario_id = ?) as media_avaliacoes_recebidas
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                pstm.setString(i, usuarioId);
            }

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("total_projetos", rs.getLong("total_projetos"));
                    estatisticas.put("total_tarefas", rs.getLong("total_tarefas"));
                    estatisticas.put("total_solucoes", rs.getLong("total_solucoes"));
                    estatisticas.put("total_avaliacoes", rs.getLong("total_avaliacoes"));
                    estatisticas.put("media_avaliacoes", rs.getDouble("media_avaliacoes_recebidas"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter estatísticas do usuário: " + e.getMessage());
        }
        return estatisticas;
    }

    public List<Usuario> buscarUsuariosAtivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.* 
            FROM usuario u 
            WHERE u.ativo = true AND EXISTS (
                SELECT 1 FROM tarefas t 
                WHERE t.usuario_responsavel_id = u.id 
                AND t.status = 'EM_ANDAMENTO'
            )
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                usuarios.add(criarUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários ativos: " + e.getMessage());
        }
        return usuarios;
    }

    public void atualizarPerfilUsuario(String usuarioId, PerfilUsuario perfil) {
        String sql = """
            INSERT INTO perfil_usuario (usuario_id, biografia, foto_perfil_uri, habilidades)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            biografia = VALUES(biografia),
            foto_perfil_uri = VALUES(foto_perfil_uri),
            habilidades = VALUES(habilidades)
        """;

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, usuarioId);
            pstm.setString(2, perfil.getBiografia());
            pstm.setString(3, perfil.getFotoPerfilUri());
            pstm.setString(4, String.join(",", perfil.getHabilidades()));
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar perfil do usuário: " + e.getMessage());
        }
    }
}
