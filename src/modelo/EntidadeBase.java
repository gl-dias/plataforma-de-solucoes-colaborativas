package modelo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EntidadeBase implements InterfaceEntidadeBase {
    private String id;
    // Contador estático compartilhado por todas as entidades para gerar IDs incrementais
    private static final AtomicInteger contador = new AtomicInteger(1);

    protected EntidadeBase() {
        // Gera um ID incremental automaticamente
        this.id = String.valueOf(contador.getAndIncrement());
    }

    protected EntidadeBase(String id) {
        this.id = id != null ? id : String.valueOf(contador.getAndIncrement());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID não pode estar vazio");
        }
        this.id = id;
    }

    /**
     * Método abstrato que deve ser implementado por todas as subclasses
     * para fornecer uma descrição significativa da entidade.
     * @return String com a descrição da entidade
     */
    public abstract String getDescricaoEntidade();

    /**
     * Verifica se a entidade é válida para persistência
     * @return true se a entidade estiver válida, false caso contrário
     */
    public abstract boolean isValid();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntidadeBase that = (EntidadeBase) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
