package dao;

import java.util.ArrayList;

public interface BaseDAO<T> {
    void salvar(T objeto);
    Object buscarPorId(String id);
    ArrayList<T> listarTodosLazyLoading();
    void atualizar(T objeto);
    void excluir(String id);
}