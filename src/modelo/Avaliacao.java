package modelo;

import java.sql.Timestamp;
import java.util.Objects;

public class Avaliacao extends EntidadeBase {
    private int nota;
    private String comentario;
    private String solucaoId;
    private String usuarioAvaliadorId;
    private Timestamp dataAvaliacao;
    private Usuario usuarioAvaliador;
    private Solucao solucaoAvaliada;

    public Avaliacao() {
        super(); // Chama o construtor da classe pai.
    }

    public Avaliacao(String id, int nota, String comentario, String solucaoId, String usuarioAvaliadorId) {
        super(id);
        this.nota = nota;
        this.comentario = comentario;
        this.solucaoId = solucaoId;
        this.usuarioAvaliadorId = usuarioAvaliadorId;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 5");
        }
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getSolucaoId() {
        return solucaoId;
    }

    public void setSolucaoId(String solucaoId) {
        this.solucaoId = solucaoId;
    }

    public String getUsuarioAvaliadorId() {
        return usuarioAvaliadorId;
    }

    public void setUsuarioAvaliadorId(String usuarioAvaliadorId) {
        this.usuarioAvaliadorId = usuarioAvaliadorId;
    }

    public Timestamp getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Timestamp dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Usuario getUsuarioAvaliador() {
        return usuarioAvaliador;
    }

    public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
        this.usuarioAvaliador = usuarioAvaliador;
        if (usuarioAvaliador != null) {
            this.usuarioAvaliadorId = usuarioAvaliador.getId();
        }
    }

    public Solucao getSolucaoAvaliada() {
        return solucaoAvaliada;
    }

    public void setSolucaoAvaliada(Solucao solucaoAvaliada) {
        this.solucaoAvaliada = solucaoAvaliada;
        if (solucaoAvaliada != null) {
            this.solucaoId = solucaoAvaliada.getId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(getId(), avaliacao.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "id='" + getId() + '\'' +
                ", nota=" + nota +
                ", comentario='" + comentario + '\'' +
                ", dataAvaliacao=" + dataAvaliacao +
                '}';
    }
}
