package Domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date = LocalDateTime.now();
    Long id_user_1;
    Long id_user_2;
    String status;


    public Friendship(Long id_user_1, Long id_user_2) {
        this.id_user_1 = id_user_1;
        this.id_user_2 = id_user_2;
        this.date = LocalDateTime.now();
        this.status = "Requested";
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
    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() { return status; }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
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


