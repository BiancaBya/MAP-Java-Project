package Repository;

import Domain.Entity;
import Utils.Paging.Page;
import Utils.Paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E>{
    public Page<E> findAllOnPage(Pageable pageable);
}

