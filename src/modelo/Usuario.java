package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Usuario() {
        super();
        this.projetos = new ArrayList<>();
        this.tarefasResponsavel = new ArrayList<>();
        this.solucoes = new ArrayList<>();
        this.avaliacoesRealizadas = new ArrayList<>();
        this.ativo = true;
    }

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

    public int contarTarefasConcluidas() {
        return (int) tarefasResponsavel.stream()
                .filter(Tarefa::estaConcluida)
                .count();
    }

    public double calcularMediaAvaliacoesRecebidas() {
        return solucoes.stream()
                .mapToDouble(Solucao::calcularMediaAvaliacoes)
                .average()
                .orElse(0.0);
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