package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tarefa extends EntidadeBase {
    private String titulo;
    private String descricao;
    private String status;
    private String prioridade;
    private String projetoId;
    private String usuarioResponsavelId;
    private Timestamp dataCriacao;
    private Timestamp dataConclusao;
    private List<Solucao> solucoes;
    private Usuario responsavel;
    private Projeto projeto;

    public Tarefa() {
        super();
        this.solucoes = new ArrayList<>();
        this.status = "PENDENTE";
        this.prioridade = "MEDIA";
    }

    public Tarefa(String id, String titulo, String descricao, String projetoId, String usuarioResponsavelId) {
        super(id);
        this.titulo = titulo;
        this.descricao = descricao;
        this.projetoId = projetoId;
        this.usuarioResponsavelId = usuarioResponsavelId;
        this.solucoes = new ArrayList<>();
        this.status = "PENDENTE";
        this.prioridade = "MEDIA";
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

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        if (prioridade == null || prioridade.trim().isEmpty()) {
            throw new IllegalArgumentException("A prioridade não pode estar vazia");
        }
        this.prioridade = prioridade;
    }

    public String getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(String projetoId) {
        this.projetoId = projetoId;
    }

    public String getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(String usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Timestamp getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Timestamp dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public List<Solucao> getSolucoes() {
        return new ArrayList<>(solucoes); // Retorna uma cópia da lista
    }

    public void adicionarSolucao(Solucao solucao) {
        if (solucao == null) {
            throw new IllegalArgumentException("A solução não pode ser nula");
        }
        this.solucoes.add(solucao);
    }

    public void removerSolucao(Solucao solucao) {
        this.solucoes.remove(solucao);
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
        if (responsavel != null) {
            this.usuarioResponsavelId = responsavel.getId();
        }
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        if (projeto != null) {
            this.projetoId = projeto.getId();
        }
    }

    public boolean estaConcluida() {
        return "CONCLUIDA".equals(status);
    }

    public boolean estaAtrasada() {
        if (dataConclusao == null || estaConcluida()) {
            return false;
        }
        return new Timestamp(System.currentTimeMillis()).after(dataConclusao);
    }

    public Solucao getMelhorSolucao() {
        return solucoes.stream()
                .max((s1, s2) -> Double.compare(s1.calcularMediaAvaliacoes(), s2.calcularMediaAvaliacoes()))
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarefa tarefa = (Tarefa) o;
        return Objects.equals(getId(), tarefa.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id='" + getId() + '\'' +
                ", titulo='" + titulo + '\'' +
                ", status='" + status + '\'' +
                ", prioridade='" + prioridade + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataConclusao=" + dataConclusao +
                '}';
    }
}
