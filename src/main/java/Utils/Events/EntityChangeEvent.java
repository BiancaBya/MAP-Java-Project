package Utils.Events;

import Domain.Utilizator;
import Domain.Friendship;

public class EntityChangeEvent implements Event {

    private ChangeEventType type;
    private Utilizator userData, userOldData;
    private Friendship friendshipData, friendOldData;


    public EntityChangeEvent(ChangeEventType type, Utilizator userData) {
        this.type = type;
        this.userData = userData;
    }

    public EntityChangeEvent(ChangeEventType type, Utilizator userData, Utilizator userOldData) {
        this.type = type;
        this.userData = userData;
        this.userOldData = userOldData;
    }

    public EntityChangeEvent(ChangeEventType type, Friendship friendshipData) {
        this.type = type;
        this.friendshipData = friendshipData;
    }

    public EntityChangeEvent(ChangeEventType type, Friendship friendshipData, Friendship friendOldData) {
        this.type = type;
        this.friendshipData = friendshipData;
        this.friendOldData = friendOldData;
    }


    public ChangeEventType getType() {
        return type;
    }
    public Utilizator getUserData() {
        return userData;
    }

    public Utilizator getUserOldData() {
        return userOldData;
    }

    public Friendship getFriendshipData() {
        return friendshipData;
    }

    public Friendship getFriendOldData() {
        return friendOldData;
    }

}


