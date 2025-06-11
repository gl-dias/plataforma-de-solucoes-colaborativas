package modelo;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

public class PerfilUsuario extends EntidadeBase {
    private String biografia;
    private String fotoPerfilUri;
    private Set<String> habilidades;
    private String usuarioId;
    private Usuario usuario;

    public PerfilUsuario() {
        super();
        this.habilidades = new HashSet<>();
    }

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

    public boolean temHabilidade(String habilidade) {
        return habilidade != null &&
               this.habilidades.contains(habilidade.trim().toLowerCase());
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
