package edu.rafael.dscatalog.services.validation;

import edu.rafael.dscatalog.dto.UserDTO;
import edu.rafael.dscatalog.dto.UserInsertDTO;
import edu.rafael.dscatalog.dto.UserUpdateDTO;
import edu.rafael.dscatalog.entities.User;
import edu.rafael.dscatalog.repositories.UserRepository;
import edu.rafael.dscatalog.resources.exceptions.FieldMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }


    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long userId = Long.parseLong(uriVars.get("id"));
        List<FieldMessage> list = new ArrayList<>();
        User user = repository.findByEmail(dto.getEmail());
        if(user != null && userId.equals(user.getId())){
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
