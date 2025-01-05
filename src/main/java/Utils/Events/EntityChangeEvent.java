package Utils.Events;

import Domain.User;
import Domain.Friendship;

public class EntityChangeEvent implements Event {

    private ChangeEventType type;
    private User userData, userOldData;
    private Friendship friendshipData, friendOldData;


    public EntityChangeEvent(ChangeEventType type, User userData) {
        this.type = type;
        this.userData = userData;
    }

    public EntityChangeEvent(ChangeEventType type, User userData, User userOldData) {
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
    public User getUserData() {
        return userData;
    }

    public User getUserOldData() {
        return userOldData;
    }

    public Friendship getFriendshipData() {
        return friendshipData;
    }

    public Friendship getFriendOldData() {
        return friendOldData;
    }

}


