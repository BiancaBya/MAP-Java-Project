package Domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime friendsFrom = LocalDateTime.now();
    Long id_user_1;
    Long id_user_2;

    // de facut cu id
    public Friendship(Long id_user_1, Long id_user_2) {
        this.id_user_1 = id_user_1;
        this.id_user_2 = id_user_2;
        this.friendsFrom = LocalDateTime.now();
    }

    public Long getId_user_1(){
        return id_user_1;
    }

    public Long getId_user_2(){
        return id_user_2;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    @Override
    public boolean equals(Object o){

        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        Friendship that = (Friendship) o;

        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}


