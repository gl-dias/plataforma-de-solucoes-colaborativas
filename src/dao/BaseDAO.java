package dao;

import java.util.ArrayList;

public interface BaseDAO {

    void salvar(Object objeto);

    Object buscarPorId(String id);

    ArrayList<Object> listarTodosLazyLoading();

    void atualizar(Object objeto);

    void excluir(String id);
}
