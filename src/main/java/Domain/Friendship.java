package Domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date = LocalDateTime.now();
    Long id_user_1;
    Long id_user_2;
    String status;
    Long id_request;


    public Friendship(Long id_user_1, Long id_user_2, Long id_request) {
        this.id_user_1 = id_user_1;
        this.id_user_2 = id_user_2;
        this.date = LocalDateTime.now();
        this.status = "Requested";
        this.id_request = id_request;
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

    public Long getId_request() {
        return id_request;
    }

    public void setId_request(Long id_request) {
        this.id_request = id_request;
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



