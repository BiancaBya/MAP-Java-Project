package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{

    private String firstName;
    private String lastName;
    private String password;
    private List<User> friends;
    private String email;

    public User(String firstName, String lastName, String password, String email) {
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = new ArrayList<>(friends);
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    public void removeFriend(User friend) {
        friends.remove(friend);
    }

    @Override
    public String toString() {

        StringBuilder prieteni = new StringBuilder();
        for (User friend : friends) {
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
        if (!(o instanceof User)) return false;
        User that = (User) o;

        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}



