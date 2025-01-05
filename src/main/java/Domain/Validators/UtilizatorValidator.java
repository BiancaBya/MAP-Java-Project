package Domain.Validators;

import Domain.User;

public class UtilizatorValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {

        if(entity.getFirstName().equals(""))
            throw new ValidationException("Invalid first name");

        if(entity.getLastName().equals(""))
            throw new ValidationException("Invalid last name");
    }
}

