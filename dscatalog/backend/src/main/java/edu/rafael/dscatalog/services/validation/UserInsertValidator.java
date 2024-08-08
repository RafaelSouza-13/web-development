package edu.rafael.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import edu.rafael.dscatalog.entities.User;
import edu.rafael.dscatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import edu.rafael.dscatalog.dto.UserInsertDTO;
import edu.rafael.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();
        User user = repository.findByEmail(dto.getEmail());
        if(user != null){
            list.add(new FieldMessage("email", "Este email ja existe"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}
