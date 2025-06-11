package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa uma tarefa dentro de um projeto.
 * Uma tarefa pode ter várias soluções propostas e está associada a um projeto e a um usuário responsável.
 */
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

    /**
     * Construtor padrão que inicializa as listas e define valores padrão
     * para o status e prioridade.
     */
    public Tarefa() {
        super();
        this.solucoes = new ArrayList<>();
        this.status = "PENDENTE";
        this.prioridade = "MEDIA";
    }

    /**
     * Construtor completo para criação de uma nova tarefa
     *
     * @param id Identificador único da tarefa
     * @param titulo Título da tarefa
     * @param descricao Descrição detalhada da tarefa
     * @param projetoId Identificador do projeto ao qual a tarefa pertence
     * @param usuarioResponsavelId Identificador do usuário responsável pela tarefa
     */
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

    /**
     * Verifica se a tarefa está concluída
     *
     * @return true se o status for "CONCLUIDA", false caso contrário
     */
    public boolean estaConcluida() {
        return "CONCLUIDA".equals(status);
    }

    /**
     * Verifica se a tarefa está atrasada
     *
     * @return true se a data de conclusão estiver no passado e a tarefa não estiver concluída
     */
    public boolean estaAtrasada() {
        if (dataConclusao == null || estaConcluida()) {
            return false;
        }
        return new Timestamp(System.currentTimeMillis()).after(dataConclusao);
    }

    /**
     * Obtém a melhor solução para esta tarefa com base na média das avaliações
     *
     * @return A solução com maior média de avaliações, ou null se não houver soluções
     */
    public Solucao getMelhorSolucao() {
        return solucoes.stream()
                .max((s1, s2) -> Double.compare(s1.calcularMediaAvaliacoes(), s2.calcularMediaAvaliacoes()))
                .orElse(null);
    }

    /**
     * Conclui a tarefa.
     * Este método define o status como "CONCLUIDA" e registra a data de conclusão.
     */
    public void concluirTarefa() {
        this.status = "CONCLUIDA";
        this.dataConclusao = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Conclui a tarefa e adiciona um comentário.
     * Este método é uma sobrecarga que permite adicionar um comentário ao concluir.
     *
     * @param comentario Comentário sobre a conclusão da tarefa
     */
    public void concluirTarefa(String comentario) {
        concluirTarefa();
        if (comentario != null && !comentario.trim().isEmpty()) {
            // Em uma implementação real, armazenaria este comentário em um atributo ou entidade relacionada
            System.out.println("Tarefa concluída com comentário: " + comentario);
        }
    }

    /**
     * Calcula o tempo restante para a conclusão da tarefa em dias
     *
     * @return Número de dias restantes ou -1 se não houver data de conclusão definida
     */
    public long calcularDiasRestantes() {
        if (dataConclusao == null) {
            return -1;
        }

        long diferenca = dataConclusao.getTime() - System.currentTimeMillis();
        return diferenca / (24 * 60 * 60 * 1000);
    }

    @Override
    public String getDescricaoEntidade() {
        return "Tarefa: " + titulo + " - Prioridade: " + prioridade + " - Status: " + status;
    }

    @Override
    public boolean isValid() {
        return titulo != null && !titulo.trim().isEmpty() &&
               projetoId != null && !projetoId.trim().isEmpty() &&
               usuarioResponsavelId != null && !usuarioResponsavelId.trim().isEmpty();
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
