package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Utilizator extends Entity<Long>{

    private String firstName;
    private String lastName;
    private String password;
    private List<Utilizator> friends;
    private String email;

    public Utilizator(String firstName, String lastName, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.friends = new ArrayList<>();
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void setFriends(List<Utilizator> friends) {
        this.friends = new ArrayList<>(friends);
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public void addFriend(Utilizator friend) {
        this.friends.add(friend);
    }

    public void removeFriend(Utilizator friend) {
        friends.remove(friend);
    }

    @Override
    public String toString() {

        StringBuilder prieteni = new StringBuilder();
        for (Utilizator friend : friends) {
            prieteni.append(friend.getFirstName());
            prieteni.append(" ");
            prieteni.append(friend.getLastName());
            prieteni.append(", ");
        }
        return "Utilizator{" +
                "firstName = " + firstName + '\'' +
                ", lastName = " + lastName + '\'' + ", friends = " + prieteni + "}";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;

        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}



