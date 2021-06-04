package com.Messenger.Controllers;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.Messenger.Settings;
import com.Messenger.Models.User;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;

@Controller
public class RegistrationController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
	@Autowired public JavaMailSender emailSender;
    
    @GetMapping("/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }
    
    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Validated User user, BindingResult bindingResult, Model model) {
    	if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        if (!userService.saveUser(user)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }
        
        sendConfirmationEmail(user);

        return "redirect:login";
    }
    
    @GetMapping("/confirm/{userId}")
    public String confirm(@PathVariable("userId") Long userId) {
        if (userRepo.existsById(userId)) {
        	var user = userRepo.findById(userId).get();
        	user.setConfirmed(true);
        	userRepo.save(user);
        	System.out.println("User %s confirms registration".formatted(user.getUsername()));
        }
        
        return "redirect:/";
    }
    
    public void sendConfirmationEmail(User user) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	        String htmlMsg = "<p>Подтвердите вашу регистрацию <a href=%s/confirm/%d>здесь</a></p>".formatted(Settings.URL, user.getId());
	        message.setHeader("Content-Type", "text/html; charset=UTF-8");
            message.setContent(htmlMsg, "text/html; charset=UTF-8");
	        helper.setFrom(Settings.EMAIL_NAME);
	        helper.setTo(user.getEmail());
	        helper.setSubject("Подтвердите регистрацию");

	        emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
}
