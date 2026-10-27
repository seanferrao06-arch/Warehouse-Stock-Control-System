package warehouse.interfaces;

import java.util.List;
import java.util.Optional;

/** Basic persistence operations for an entity identified by an integer key. */
public interface Crud<T> {
    T save(T entity);
    Optional<T> findById(int id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(int id);
}
