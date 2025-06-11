package modelo;

import java.util.Objects;
import java.util.UUID;

public abstract class EntidadeBase implements InterfaceEntidadeBase {
    private String id;

    protected EntidadeBase() {
        this.id = UUID.randomUUID().toString();
    }

    protected EntidadeBase(String id) {
        this.id = id != null ? id : UUID.randomUUID().toString();
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
