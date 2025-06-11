package modelo;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Classe que representa uma avaliação feita por um usuário a uma solução.
 * Uma avaliação contém uma nota, um comentário opcional e referências ao usuário avaliador e à solução avaliada.
 */
public class Avaliacao extends EntidadeBase {
    private int nota;
    private String comentario;
    private String solucaoId;
    private String usuarioAvaliadorId;
    private Timestamp dataAvaliacao;
    private Usuario usuarioAvaliador;
    private Solucao solucaoAvaliada;

    /**
     * Construtor padrão que inicializa a entidade com ID gerado automaticamente
     */
    public Avaliacao() {
        super(); // Chama o construtor da classe pai.
    }

    /**
     * Construtor completo para criação de uma nova avaliação
     *
     * @param id Identificador único da avaliação
     * @param nota Nota atribuída (de 0 a 5)
     * @param comentario Comentário opcional sobre a avaliação
     * @param solucaoId Identificador da solução avaliada
     * @param usuarioAvaliadorId Identificador do usuário avaliador
     */
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

    /**
     * Verifica se a avaliação é positiva (nota maior ou igual a 3)
     *
     * @return true se a nota for maior ou igual a 3, false caso contrário
     */
    public boolean isAvaliaoPositiva() {
        return nota >= 3;
    }

    /**
     * Atualiza a avaliação com uma nova nota
     *
     * @param novaNota Nova nota a ser atribuída
     */
    public void atualizarAvaliacao(int novaNota) {
        setNota(novaNota);
        this.dataAvaliacao = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Atualiza a avaliação com uma nova nota e comentário
     * Este método é uma sobrecarga que permite atualizar tanto a nota quanto o comentário
     *
     * @param novaNota Nova nota a ser atribuída
     * @param novoComentario Novo comentário a ser atribuído
     */
    public void atualizarAvaliacao(int novaNota, String novoComentario) {
        setNota(novaNota);
        setComentario(novoComentario);
        this.dataAvaliacao = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Verifica se uma avaliação pode ser considerada como "recomendação premium"
     * Este método classifica as avaliações de acordo com critérios específicos
     *
     * @return true se a avaliação for "premium"
     */
    public boolean isRecomendacaoPremium() {
        return nota >= 4 && comentario != null && comentario.length() >= 20;
    }

    /**
     * Verifica se uma avaliação pode ser considerada como uma recomendação de determinado nível
     * Este método é sobrecarregado para permitir diferentes critérios de classificação
     *
     * @param nivel Nível de exigência (1: alta, 2: média, 3: baixa)
     * @return true se a avaliação atender aos critérios do nível especificado
     */
    public boolean isRecomendacao(int nivel) {
        switch(nivel) {
            case 1: // Alta exigência
                return nota == 5 && comentario != null && comentario.length() >= 30;
            case 2: // Média exigência
                return nota >= 4 && comentario != null && comentario.length() >= 10;
            case 3: // Baixa exigência
                return nota >= 3;
            default:
                return isAvaliaoPositiva();
        }
    }

    @Override
    public String getDescricaoEntidade() {
        String classificacao = isAvaliaoPositiva() ? "Positiva" : "Negativa";
        return "Avaliação: Nota " + nota + " - " + classificacao +
               (comentario != null && !comentario.isEmpty() ? " - Com comentário" : " - Sem comentário");
    }

    @Override
    public boolean isValid() {
        return nota >= 0 && nota <= 5 &&
               solucaoId != null && !solucaoId.trim().isEmpty() &&
               usuarioAvaliadorId != null && !usuarioAvaliadorId.trim().isEmpty();
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
