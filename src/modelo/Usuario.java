package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa um usuário no sistema.
 * Um usuário pode criar projetos, ser responsável por tarefas, propor soluções e realizar avaliações.
 */
public class Usuario extends EntidadeBase {
    private String nome;
    private String email;
    private String senhaCriptografada;
    private boolean ativo;
    private Timestamp dataCadastro;
    private PerfilUsuario perfil;
    private List<Projeto> projetos;
    private List<Tarefa> tarefasResponsavel;
    private List<Solucao> solucoes;
    private List<Avaliacao> avaliacoesRealizadas;

    /**
     * Construtor padrão que inicializa as coleções
     */
    public Usuario() {
        super();
        this.projetos = new ArrayList<>();
        this.tarefasResponsavel = new ArrayList<>();
        this.solucoes = new ArrayList<>();
        this.avaliacoesRealizadas = new ArrayList<>();
        this.ativo = true;
    }

    /**
     * Construtor completo para criação de um novo usuário
     *
     * @param id Identificador único do usuário
     * @param nome Nome completo do usuário
     * @param email Email de contato do usuário
     * @param senhaCriptografada Senha do usuário (já criptografada)
     */
    public Usuario(String id, String nome, String email, String senhaCriptografada) {
        super(id);
        this.nome = nome;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.projetos = new ArrayList<>();
        this.tarefasResponsavel = new ArrayList<>();
        this.solucoes = new ArrayList<>();
        this.avaliacoesRealizadas = new ArrayList<>();
        this.ativo = true;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode estar vazio");
        }
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email não pode estar vazio");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }

    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    public void setSenhaCriptografada(String senhaCriptografada) {
        if (senhaCriptografada == null || senhaCriptografada.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode estar vazia");
        }
        this.senhaCriptografada = senhaCriptografada;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public List<Projeto> getProjetos() {
        return new ArrayList<>(projetos);
    }

    public void adicionarProjeto(Projeto projeto) {
        if (projeto == null) {
            throw new IllegalArgumentException("O projeto não pode ser nulo");
        }
        this.projetos.add(projeto);
    }

    public void removerProjeto(Projeto projeto) {
        this.projetos.remove(projeto);
    }

    public List<Tarefa> getTarefasResponsavel() {
        return new ArrayList<>(tarefasResponsavel);
    }

    public void adicionarTarefaResponsavel(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula");
        }
        this.tarefasResponsavel.add(tarefa);
    }

    public void removerTarefaResponsavel(Tarefa tarefa) {
        this.tarefasResponsavel.remove(tarefa);
    }

    public List<Solucao> getSolucoes() {
        return new ArrayList<>(solucoes);
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

    public List<Avaliacao> getAvaliacoesRealizadas() {
        return new ArrayList<>(avaliacoesRealizadas);
    }

    public void adicionarAvaliacaoRealizada(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("A avaliação não pode ser nula");
        }
        this.avaliacoesRealizadas.add(avaliacao);
    }

    public void removerAvaliacaoRealizada(Avaliacao avaliacao) {
        this.avaliacoesRealizadas.remove(avaliacao);
    }

    /**
     * Conta o número de tarefas concluídas pelo usuário
     *
     * @return Número de tarefas concluídas
     */
    public int contarTarefasConcluidas() {
        return (int) tarefasResponsavel.stream()
                .filter(Tarefa::estaConcluida)
                .count();
    }

    /**
     * Calcula a média das avaliações recebidas nas soluções propostas pelo usuário
     *
     * @return Média das avaliações ou 0 se não houver avaliações
     */
    public double calcularMediaAvaliacoesRecebidas() {
        return solucoes.stream()
                .mapToDouble(Solucao::calcularMediaAvaliacoes)
                .average()
                .orElse(0.0);
    }

    /**
     * Verifica se uma senha corresponde à senha armazenada do usuário.
     * Este método permite sobrecarregar a verificação de senhas com diferentes algoritmos.
     *
     * @param senhaTexto Senha em texto plano para verificação
     * @return true se a senha for válida, false caso contrário
     */
    public boolean verificarSenha(String senhaTexto) {
        // Em uma implementação real, usaria BCrypt ou outro algoritmo seguro
        return senhaTexto != null && senhaCriptografada.equals(senhaTexto);
    }

    /**
     * Método sobrecarregado que permite verificar a senha com um algoritmo específico
     *
     * @param senhaTexto Senha em texto plano para verificação
     * @param algoritmo Nome do algoritmo a ser utilizado
     * @return true se a senha for válida, false caso contrário
     */
    public boolean verificarSenha(String senhaTexto, String algoritmo) {
        // Em uma implementação real, utilizaria o algoritmo especificado
        if ("BCRYPT".equalsIgnoreCase(algoritmo)) {
            // Implementação específica para BCrypt
            return senhaTexto != null && senhaCriptografada.equals(senhaTexto);
        } else {
            // Algoritmo padrão
            return verificarSenha(senhaTexto);
        }
    }

    @Override
    public String getDescricaoEntidade() {
        return "Usuário: " + nome + " (" + email + ")";
    }

    @Override
    public boolean isValid() {
        return nome != null && !nome.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() && email.contains("@") &&
               senhaCriptografada != null && !senhaCriptografada.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getId(), usuario.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + getId() + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}