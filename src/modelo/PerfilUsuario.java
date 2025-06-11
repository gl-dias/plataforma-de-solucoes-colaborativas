package modelo;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Classe que representa o perfil de um usuário no sistema.
 * O perfil contém informações adicionais como biografia, foto e habilidades.
 */
public class PerfilUsuario extends EntidadeBase {
    private String biografia;
    private String fotoPerfilUri;
    private Set<String> habilidades;
    private String usuarioId;
    private Usuario usuario;

    /**
     * Construtor padrão que inicializa o conjunto de habilidades
     */
    public PerfilUsuario() {
        super();
        this.habilidades = new HashSet<>();
    }

    /**
     * Construtor completo para criação de um novo perfil de usuário
     *
     * @param id Identificador único do perfil
     * @param biografia Descrição sobre o usuário
     * @param fotoPerfilUri URI para a foto do usuário
     */
    public PerfilUsuario(String id, String biografia, String fotoPerfilUri) {
        super(id);
        this.biografia = biografia;
        this.fotoPerfilUri = fotoPerfilUri;
        this.habilidades = new HashSet<>();
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getFotoPerfilUri() {
        return fotoPerfilUri;
    }

    public void setFotoPerfilUri(String fotoPerfilUri) {
        this.fotoPerfilUri = fotoPerfilUri;
    }

    public Set<String> getHabilidades() {
        return new HashSet<>(habilidades); // Retorna uma cópia do conjunto
    }

    public void adicionarHabilidade(String habilidade) {
        if (habilidade == null || habilidade.trim().isEmpty()) {
            throw new IllegalArgumentException("A habilidade não pode estar vazia");
        }
        this.habilidades.add(habilidade.trim().toLowerCase());
    }

    public void removerHabilidade(String habilidade) {
        if (habilidade != null) {
            this.habilidades.remove(habilidade.trim().toLowerCase());
        }
    }

    public void setHabilidades(Set<String> habilidades) {
        if (habilidades == null) {
            this.habilidades = new HashSet<>();
        } else {
            this.habilidades = new HashSet<>(habilidades);
        }
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.usuarioId = usuario.getId();
        }
    }

    /**
     * Verifica se o usuário possui determinada habilidade
     *
     * @param habilidade Nome da habilidade a ser verificada
     * @return true se o usuário possuir a habilidade, false caso contrário
     */
    public boolean temHabilidade(String habilidade) {
        return habilidade != null &&
               this.habilidades.contains(habilidade.trim().toLowerCase());
    }

    /**
     * Verifica se o usuário possui determinada habilidade com um nível específico
     * Este método é uma sobrecarga que permite verificar o nível de proficiência
     *
     * @param habilidade Nome da habilidade a ser verificada
     * @param nivelMinimo Nível mínimo de proficiência requerido
     * @return true se o usuário possuir a habilidade no nível especificado
     */
    public boolean temHabilidade(String habilidade, int nivelMinimo) {
        // Em uma implementação real, armazenaria o nível de cada habilidade
        // Por enquanto, estamos apenas exemplificando o polimorfismo
        if (nivelMinimo <= 1) {
            return temHabilidade(habilidade);
        }
        // Simulando um critério mais rigoroso para níveis mais altos
        return temHabilidade(habilidade) && biografia != null &&
               biografia.toLowerCase().contains(habilidade.toLowerCase());
    }

    /**
     * Verifica se o perfil está completo com informações suficientes
     *
     * @return true se o perfil estiver completo, false caso contrário
     */
    public boolean isPerfilCompleto() {
        return biografia != null && !biografia.trim().isEmpty() &&
               !habilidades.isEmpty();
    }

    /**
     * Retorna o nível de completude do perfil
     *
     * @return Percentual de completude do perfil (0-100)
     */
    public int calcularCompletudePercentual() {
        int pontos = 0;
        int total = 3; // Total de critérios avaliados

        if (biografia != null && !biografia.trim().isEmpty()) pontos++;
        if (fotoPerfilUri != null && !fotoPerfilUri.trim().isEmpty()) pontos++;
        if (!habilidades.isEmpty()) pontos++;

        return (pontos * 100) / total;
    }

    /**
     * Calcula a compatibilidade deste perfil com outro baseado nas habilidades em comum
     *
     * @param outroPerfil Outro perfil para comparação
     * @return Percentual de compatibilidade (0-100)
     */
    public int calcularCompatibilidade(PerfilUsuario outroPerfil) {
        if (outroPerfil == null || habilidades.isEmpty() || outroPerfil.habilidades.isEmpty()) {
            return 0;
        }

        Set<String> habilidadesComuns = new HashSet<>(habilidades);
        habilidadesComuns.retainAll(outroPerfil.habilidades);

        Set<String> todasHabilidades = new HashSet<>(habilidades);
        todasHabilidades.addAll(outroPerfil.habilidades);

        return todasHabilidades.isEmpty() ? 0 : (habilidadesComuns.size() * 100) / todasHabilidades.size();
    }

    @Override
    public String getDescricaoEntidade() {
        return "Perfil de usuário" +
               (usuario != null ? " de " + usuario.getNome() : "") +
               " - Habilidades: " + habilidades.size() +
               " - Completude: " + calcularCompletudePercentual() + "%";
    }

    @Override
    public boolean isValid() {
        return usuarioId != null && !usuarioId.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerfilUsuario perfil = (PerfilUsuario) o;
        return Objects.equals(getId(), perfil.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "PerfilUsuario{" +
                "id='" + getId() + '\'' +
                ", biografia='" + biografia + '\'' +
                ", habilidades=" + habilidades +
                '}';
    }
}
