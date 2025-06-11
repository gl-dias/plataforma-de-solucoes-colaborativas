package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa um projeto na plataforma de soluções colaborativas.
 * Um projeto contém um conjunto de tarefas e está associado a um usuário responsável.
 */
public class Projeto extends EntidadeBase {
    private String titulo;
    private String descricao;
    private String status;
    private String usuarioId;
    private Timestamp dataCriacao;
    private Timestamp dataConclusao;
    private List<Tarefa> tarefas;
    private Usuario responsavel;

    /**
     * Construtor padrão que inicializa a lista de tarefas e define o status inicial
     */
    public Projeto() {
        super();
        this.tarefas = new ArrayList<>();
        this.status = "EM_ANDAMENTO";
    }

    /**
     * Construtor completo para criação de um novo projeto
     *
     * @param id Identificador único do projeto
     * @param titulo Título do projeto
     * @param descricao Descrição detalhada do projeto
     * @param usuarioId Identificador do usuário responsável pelo projeto
     */
    public Projeto(String id, String titulo, String descricao, String usuarioId) {
        super(id);
        this.titulo = titulo;
        this.descricao = descricao;
        this.usuarioId = usuarioId;
        this.tarefas = new ArrayList<>();
        this.status = "EM_ANDAMENTO";
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

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
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

    public List<Tarefa> getTarefas() {
        return new ArrayList<>(tarefas); // Retorna uma cópia da lista para evitar modificações externas
    }

    public void adicionarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula");
        }
        this.tarefas.add(tarefa);
    }

    public void removerTarefa(Tarefa tarefa) {
        this.tarefas.remove(tarefa);
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
        if (responsavel != null) {
            this.usuarioId = responsavel.getId();
        }
    }

    /**
     * Calcula o percentual de progresso do projeto baseado nas tarefas concluídas
     *
     * @return Percentual de conclusão do projeto (0-100)
     */
    public double calcularProgresso() {
        if (tarefas.isEmpty()) {
            return 0.0;
        }
        long tarefasConcluidas = tarefas.stream()
                .filter(tarefa -> "CONCLUIDA".equals(tarefa.getStatus()))
                .count();
        return (double) tarefasConcluidas / tarefas.size() * 100;
    }

    /**
     * Verifica se o projeto está concluído
     *
     * @return true se o status for "CONCLUIDO", false caso contrário
     */
    public boolean estaConcluido() {
        return "CONCLUIDO".equals(status);
    }

    /**
     * Atualiza o status do projeto com base no progresso das tarefas.
     * Este método é uma sobrecarga que permite especificar um limiar para considerar o projeto como concluído.
     *
     * @param limiarConclusao Percentual mínimo para considerar o projeto como concluído
     * @return O novo status do projeto
     */
    public String atualizarStatus(double limiarConclusao) {
        double progresso = calcularProgresso();
        if (progresso >= limiarConclusao) {
            this.status = "CONCLUIDO";
            this.dataConclusao = new Timestamp(System.currentTimeMillis());
        } else if (progresso > 0) {
            this.status = "EM_ANDAMENTO";
        } else {
            this.status = "NAO_INICIADO";
        }
        return this.status;
    }

    /**
     * Atualiza o status do projeto com base no progresso das tarefas.
     * Utiliza o limiar padrão de 100% para considerar o projeto como concluído.
     *
     * @return O novo status do projeto
     */
    public String atualizarStatus() {
        return atualizarStatus(100.0); // Por padrão, só considera concluído quando 100%
    }

    @Override
    public String getDescricaoEntidade() {
        return "Projeto: " + titulo + " - Status: " + status;
    }

    @Override
    public boolean isValid() {
        return titulo != null && !titulo.trim().isEmpty() &&
               usuarioId != null && !usuarioId.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return Objects.equals(getId(), projeto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Projeto{" +
                "id='" + getId() + '\'' +
                ", titulo='" + titulo + '\'' +
                ", status='" + status + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataConclusao=" + dataConclusao +
                '}';
    }
}
