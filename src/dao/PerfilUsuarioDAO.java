package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.PerfilUsuario;

public class PerfilUsuarioDAO implements BaseDAO {

    private Connection connection;

    public PerfilUsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public void criarTabela() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS perfil_usuario (
                id VARCHAR(36) PRIMARY KEY,
                biografia TEXT,
                foto_perfil_uri VARCHAR(255),
                habilidades TEXT
            )
        """;

        try (java.sql.Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof PerfilUsuario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo PerfilUsuario.");
        }

        PerfilUsuario perfil = (PerfilUsuario) objeto;

        try {
            String sql = "INSERT INTO perfil_usuario (id, biografia, foto_perfil_uri, habilidades) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, perfil.getId());
                pstm.setString(2, perfil.getBiografia());
                pstm.setString(3, perfil.getFotoPerfilUri());
                // Converte o conjunto de habilidades em uma string separada por vírgulas
                String habilidadesStr = String.join(",", perfil.getHabilidades());
                pstm.setString(4, habilidadesStr);

                pstm.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object buscarPorId(String id) {
        try {
            String sql = "SELECT id, biografia, foto_perfil_uri, habilidades FROM perfil_usuario WHERE id = ?";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, id);

                pstm.execute();
                ResultSet rst = pstm.getResultSet();
                while (rst.next()) {
                    String biografia = rst.getString("biografia");
                    String fotoPerfilUri = rst.getString("foto_perfil_uri");

                    PerfilUsuario perfil = new PerfilUsuario(id, biografia, fotoPerfilUri);

                    String habilidadesStr = rst.getString("habilidades");
                    if (habilidadesStr != null && !habilidadesStr.trim().isEmpty()) {
                        for (String habilidade : habilidadesStr.split(",")) {
                            if (!habilidade.trim().isEmpty()) {
                                perfil.adicionarHabilidade(habilidade.trim());
                            }
                        }
                    }

                    return perfil;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> perfis = new ArrayList<>();

        try {
            String sql = "SELECT id, biografia, foto_perfil_uri, habilidades FROM perfil_usuario";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.execute();
                ResultSet rst = pstm.getResultSet();
                while (rst.next()) {
                    String id = rst.getString("id");
                    String biografia = rst.getString("biografia");
                    String fotoPerfilUri = rst.getString("foto_perfil_uri");

                    PerfilUsuario perfil = new PerfilUsuario(id, biografia, fotoPerfilUri);

                    String habilidadesStr = rst.getString("habilidades");
                    if (habilidadesStr != null && !habilidadesStr.isEmpty()) {
                        for (String habilidade : habilidadesStr.split(",")) {
                              perfil.adicionarHabilidade(habilidade);
                        }
                    }

                    perfis.add(perfil);
                }
            }
            return perfis;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof PerfilUsuario)) {
            throw new IllegalArgumentException("Objeto deve ser do tipo PerfilUsuario.");
        }

        PerfilUsuario perfil = (PerfilUsuario) objeto;

        try {
            String sql = "UPDATE perfil_usuario SET biografia = ?, foto_perfil_uri = ?, habilidades = ? WHERE id = ?";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, perfil.getBiografia());
                pstm.setString(2, perfil.getFotoPerfilUri());
                String habilidadesStr = perfil.getHabilidades() != null ? String.join(",", perfil.getHabilidades()) : "";
                pstm.setString(3, habilidadesStr);
                pstm.setString(4, perfil.getId());

                int linhasAfetadas = pstm.executeUpdate();

                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao atualizar: nenhuma linha foi afetada.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar perfil de usuário: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String id) {
        try {
            String sql = "DELETE FROM perfil_usuario WHERE id = ?";

            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, id);

                int linhasAfetadas = pstm.executeUpdate();

                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao deletar: nenhuma linha foi afetada.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


