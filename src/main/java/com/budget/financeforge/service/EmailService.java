package com.budget.financeforge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendActivationEmail(String to, String activationLink){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Activation Email");
        message.setText("Hello!\n" +
                "Dziękuje za rejestracje konta w FinanceForge .\n" +
                "\n" +
                "Aby dokończyć aktywacje konta kliknij w poniższy link :\n" + "www.financeforge.pl/activate/"+activationLink);

        mailSender.send(message);

    }


    public void sendResetPassword(String to, String activationLink){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Reset hasła");
        message.setText("Witaj!\n" +
                "Aby ustawić nowe hasło wejdź w poniższy link :\n" + "www.financeforge.pl/passwordReset/"+activationLink);

        mailSender.send(message);

    }



}
