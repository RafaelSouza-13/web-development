package edu.rafael.dscatalog.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError{
    private List<FieldMessage> erros = new ArrayList<>();

    public List<FieldMessage> getErros() {
        return erros;
    }

    public void addError(String fieldName, String message){
        FieldMessage field = new FieldMessage(fieldName, message);
        erros.add(field);
    }
}
