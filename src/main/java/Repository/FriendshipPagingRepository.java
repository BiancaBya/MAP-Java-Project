package Repository;

import Domain.Entity;
import Domain.User;
import Utils.Paging.Page;
import Utils.Paging.Pageable;

public interface FriendshipPagingRepository <ID, E extends Entity<ID>> extends PagingRepository<ID, E> {
    Page<E> getUsersFriends(Pageable pageable, User user);
}
