package qcp.orm.interfaces;

import java.util.List;

public interface OrmCRUD<T> {
	List<T> findAll();
	void create(T entity);
	void update(T entity);
	void delete(T entity);
}
