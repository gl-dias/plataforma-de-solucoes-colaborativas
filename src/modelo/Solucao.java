package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa uma solução proposta para uma tarefa.
 * Uma solução pode receber avaliações dos usuários e está associada a uma tarefa e a um autor.
 */
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

    /**
     * Construtor padrão que inicializa a lista de avaliações e define o status inicial.
     */
    public Solucao() {
        super();
        this.avaliacoes = new ArrayList<>();
        this.status = "PENDENTE";
    }

    /**
     * Construtor completo para criação de uma nova solução
     *
     * @param id Identificador único da solução
     * @param titulo Título da solução
     * @param descricao Descrição detalhada da solução
     * @param tarefaId Identificador da tarefa à qual a solução pertence
     * @param usuarioId Identificador do usuário autor da solução
     */
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

    /**
     * Calcula a média das avaliações recebidas por esta solução
     *
     * @return A média das notas ou 0 se não houver avaliações
     */
    public double calcularMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) {
            return 0.0;
        }
        return avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .average()
                .orElse(0.0);
    }

    /**
     * Verifica se a solução foi aprovada
     *
     * @return true se o status for "APROVADA", false caso contrário
     */
    public boolean estaAprovada() {
        return "APROVADA".equals(status);
    }

    /**
     * Aprova a solução
     * Este método define o status como "APROVADA".
     */
    public void aprovar() {
        this.status = "APROVADA";
    }

    /**
     * Aprova a solução e atualiza a tarefa associada.
     * Este método é uma sobrecarga que permite atualizar automaticamente a tarefa como concluída.
     *
     * @param concluirTarefa Se true, a tarefa associada será marcada como concluída
     */
    public void aprovar(boolean concluirTarefa) {
        aprovar();
        if (concluirTarefa && this.tarefa != null) {
            this.tarefa.concluirTarefa("Concluída com a solução: " + this.titulo);
        }
    }

    /**
     * Calcula a qualidade da solução baseada na média de avaliações
     *
     * @return Nível de qualidade ("EXCELENTE", "BOA", "REGULAR", "RUIM")
     */
    public String calcularQualidade() {
        double media = calcularMediaAvaliacoes();
        if (media >= 4.5) return "EXCELENTE";
        if (media >= 3.5) return "BOA";
        if (media >= 2.5) return "REGULAR";
        return "RUIM";
    }

    /**
     * Calcula a qualidade da solução baseada em critérios específicos
     *
     * @param criterioNotas Critério específico para classificação (1: rigoroso, 2: moderado, 3: flexível)
     * @return Nível de qualidade baseado no critério escolhido
     */
    public String calcularQualidade(int criterioNotas) {
        double media = calcularMediaAvaliacoes();

        switch (criterioNotas) {
            case 1: // Rigoroso
                if (media >= 4.7) return "EXCELENTE";
                if (media >= 4.0) return "BOA";
                if (media >= 3.0) return "REGULAR";
                return "RUIM";
            case 2: // Moderado
                return calcularQualidade(); // Usa o cálculo padrão
            case 3: // Flexível
                if (media >= 4.0) return "EXCELENTE";
                if (media >= 3.0) return "BOA";
                if (media >= 2.0) return "REGULAR";
                return "RUIM";
            default:
                return calcularQualidade(); // Usa o cálculo padrão em caso de critério inválido
        }
    }

    @Override
    public String getDescricaoEntidade() {
        return "Solução: " + titulo + " - Status: " + status + " - Qualidade: " + calcularQualidade();
    }

    @Override
    public boolean isValid() {
        return titulo != null && !titulo.trim().isEmpty() &&
               tarefaId != null && !tarefaId.trim().isEmpty() &&
               usuarioId != null && !usuarioId.trim().isEmpty();
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
