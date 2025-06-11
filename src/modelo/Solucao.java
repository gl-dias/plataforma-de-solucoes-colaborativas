package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Solucao extends EntidadeBase {
    private String titulo;
    private String descricao;
    private String status;
    private String tarefaId;
    private String usuarioId;
    private Timestamp dataSubmissao;
    private List<Avaliacao> avaliacoes;
    private Usuario autor;
    private Tarefa tarefa;

    public Solucao() {
        super();
        this.avaliacoes = new ArrayList<>();
        this.status = "PENDENTE";
    }

    public Solucao(String id, String titulo, String descricao, String tarefaId, String usuarioId) {
        super(id);
        this.titulo = titulo;
        this.descricao = descricao;
        this.tarefaId = tarefaId;
        this.usuarioId = usuarioId;
        this.avaliacoes = new ArrayList<>();
        this.status = "PENDENTE";
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode estar vazio");
        }
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("O status não pode estar vazio");
        }
        this.status = status;
    }

    public String getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(String tarefaId) {
        this.tarefaId = tarefaId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Timestamp getDataSubmissao() {
        return dataSubmissao;
    }

    public void setDataSubmissao(Timestamp dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public List<Avaliacao> getAvaliacoes() {
        return new ArrayList<>(avaliacoes); // Retorna uma cópia da lista
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("A avaliação não pode ser nula");
        }
        this.avaliacoes.add(avaliacao);
    }

    public void removerAvaliacao(Avaliacao avaliacao) {
        this.avaliacoes.remove(avaliacao);
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
        if (autor != null) {
            this.usuarioId = autor.getId();
        }
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null) {
            this.tarefaId = tarefa.getId();
        }
    }

    public double calcularMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) {
            return 0.0;
        }
        return avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .average()
                .orElse(0.0);
    }

    public boolean estaAprovada() {
        return "APROVADA".equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solucao solucao = (Solucao) o;
        return Objects.equals(getId(), solucao.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Solucao{" +
                "id='" + getId() + '\'' +
                ", titulo='" + titulo + '\'' +
                ", status='" + status + '\'' +
                ", dataSubmissao=" + dataSubmissao +
                ", mediaAvaliacoes=" + calcularMediaAvaliacoes() +
                '}';
    }
}
