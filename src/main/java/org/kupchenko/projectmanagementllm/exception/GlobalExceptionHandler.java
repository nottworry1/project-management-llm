package org.kupchenko.projectmanagementllm.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(Model model, HttpServletRequest request) {
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        model.addAttribute("statusCode", 403);
        return "error/custom-error";
    }

    @ExceptionHandler(exception = {NoHandlerFoundException.class, EntityNotFoundException.class})
    public String handleNotFound(Model model, HttpServletRequest request) {
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", "The page you are looking for does not exist.");
        model.addAttribute("statusCode", 404);
        return "error/custom-error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupported(Model model, HttpServletRequest request) {
        model.addAttribute("errorTitle", "Method Not Allowed");
        model.addAttribute("errorMessage", "This method is not supported for the requested URL.");
        model.addAttribute("statusCode", 405);
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Model model, HttpServletRequest request, Exception ex) {
        model.addAttribute("errorTitle", "Internal Server Error");
        model.addAttribute("errorMessage", "An unexpected error occurred.");
        model.addAttribute("statusCode", 500);
        return "error/custom-error";
    }
}
