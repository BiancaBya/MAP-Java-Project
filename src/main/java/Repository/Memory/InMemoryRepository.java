package Repository.Memory;

import Domain.Validators.Validator;
import Domain.Validators.ValidationException;
import Domain.Entity;
import Repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException {

        if(entity==null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");

        validator.validate(entity);
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) throws ValidationException {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null!");
        }

        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) throws ValidationException {

        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        if (entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        } else {
            return Optional.of(entity);
        }
    }
}


