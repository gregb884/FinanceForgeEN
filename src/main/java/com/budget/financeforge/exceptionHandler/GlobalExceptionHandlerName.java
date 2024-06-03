package com.budget.financeforge.exceptionHandler;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandlerName {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request, RedirectAttributes redirectAttributes) {


        System.out.println("Validation Exception caught: " + ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Error : Incorrect Attributes ");


        String referer = request.getHeader("Referer");



        return "redirect:" + referer;
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request, RedirectAttributes redirectAttributes) {


        System.out.println("Mismatch Exception caught: " + ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "Error : Incorrect Attributes ");

        String referer = request.getHeader("Referer");




        return "redirect:/panel";
    }


    @ExceptionHandler(AccessDeniedException.class)
    public String handleMethodArgumentTypeMismatchException(AccessDeniedException ex, RedirectAttributes redirectAttributes) {


        System.out.println("AccessDeniedException: " + ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to view this budget ! ");



        return "redirect:/panel";
    }


    @ExceptionHandler(RuntimeException.class)
    public String handleMethodArgumentTypeMismatchException(RuntimeException ex, RedirectAttributes redirectAttributes) {



        redirectAttributes.addFlashAttribute("errorMessage", "Item Not Find ! ");



        return "redirect:/panel";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {


        redirectAttributes.addFlashAttribute("errorMessage", " Not Found ! ");


        return "redirect:/panel";
    }







}
