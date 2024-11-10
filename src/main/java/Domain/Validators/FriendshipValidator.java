package Domain.Validators;

import Domain.Friendship;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (Objects.equals(entity.getId_user_1(), entity.getId_user_2())) {
            throw new ValidationException("Invalid Friendship");
        }
    }
}

