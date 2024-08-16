package qcp.orm.interfaces;

import java.util.UUID;

public interface OrmECRUD<T> extends OrmCRUD<T>{
	T findById(UUID entityId);
}
