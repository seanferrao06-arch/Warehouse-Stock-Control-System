package warehouse.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import warehouse.util.DatabaseException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DatabaseException.class)
    public String databaseError(DatabaseException exception, Model model) { model.addAttribute("errorMessage", exception.getMessage()); return "error"; }
    @ExceptionHandler(Exception.class)
    public String unexpectedError(Exception exception, Model model) { model.addAttribute("errorMessage", "Something went wrong. Please retry the action or contact the warehouse administrator."); return "error"; }
}
