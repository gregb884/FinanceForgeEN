package com.budget.financeforge.controller;


import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.UserDto;
import com.budget.financeforge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

@Controller
public class LoginController {


    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String getLogin(ModelMap model){

        User user = new User();
        model.put("user", user);
        return "login";
    }

    @GetMapping("/register")
    public String getRegister(ModelMap modelMap)
    {
        User user = new User();
        modelMap.put("userDto", user);
        return "register";

    }

    @PostMapping ("/register")
    public String postRegister(@ModelAttribute UserDto user, ModelMap modelMap)
    {

        if(!StringUtils.isEmpty(user.getPassword()) && !StringUtils.isEmpty(user.getConfirmPassword()))
        {
            if(!user.getPassword().equals(user.getConfirmPassword()))
            {
                modelMap.put("errorMessage", "Password don't match");
                return "register";
            }

        }

        if(StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getConfirmPassword()))
        {
            modelMap.put("errorMessage", "You must choose a password");
            return "register";
        }


        if (!userService.checkUser(user))

        {
            modelMap.put("errorMessage", "User Already Exists");
            return "register";
        }


           userService.saveUser(user);

        modelMap.put("message", "Registered Successfully, check your email to activate your account ");


        return "register";

    }

    @GetMapping("/activate/{activationCode}")
    public String activateAccount(@PathVariable String activationCode, ModelMap modelMap){


       boolean confirm = userService.confirmUser(activationCode);

       if(confirm)
       {
           modelMap.put("message", "Konto Aktywowane");

           return "login";
       }

       modelMap.put("errorMessage", "Błąd Aktywacji");

        return "login";
    }


    @PostMapping("/passwordReset")
    public String passwordReset(@ModelAttribute UserDto userDto, ModelMap modelMap){


    boolean confirm = userService.resetLink(userDto.getUsername());

        if(confirm)
        {

            modelMap.put("message", "Wysłano link do zmiany hasła na adres email");

            return "login";
        }


        modelMap.put("errorMessage", "Nie znaleziono konta, Utwórz konto");

        return "register";
    }


    @GetMapping("/passwordReset")
    public String passwordChange( ModelMap modelMap){

        modelMap.put("user", new UserDto());

        return "resetPassword";
    }



    @GetMapping("/passwordReset/{activationCode}")
    public String resetPassword(@PathVariable String activationCode, ModelMap modelMap){


        modelMap.put("activationCode", activationCode);
        UserDto userDto = new UserDto();
        modelMap.put("userDto", userDto);

        return "changePassword";
    }


    @PostMapping("/passwordReset/{activationCode}")
    public String newPasswordPost(@PathVariable String activationCode,@ModelAttribute UserDto userDto, ModelMap modelMap){



        if (userDto.getPassword().equals(userDto.getConfirmPassword()))
        {
            if(userService.changePassword(activationCode,userDto.getPassword())){

                modelMap.put("message", "Hasło zostało zmienone ,teraz możesz się zalogować");

                return "login";

            }

                modelMap.put("errorMessage", "Nie znaleziono użytkownika , zarejestruj się ");

                return "register";

        }



        modelMap.put("errorMessage", "Podane hasła różnią się !");

        return "changePassword";

    }






}



