package Domain.Validators;

import Domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {

        if(entity.getFirstName().equals(""))
            throw new ValidationException("Invalid first name");

        if(entity.getLastName().equals(""))
            throw new ValidationException("Invalid last name");
    }
}

