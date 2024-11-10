package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Utilizator extends Entity<Long>{

    private String firstName;
    private String lastName;
    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = new ArrayList<>();
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

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void setFriends(List<Utilizator> friends) {
        this.friends = new ArrayList<>(friends);
    }

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



