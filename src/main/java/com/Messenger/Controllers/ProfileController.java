package com.Messenger.Controllers;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.Messenger.Models.ChatMessage;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatType;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Models.User;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatTypeRepository;
import com.Messenger.Repo.ChatUserRelationRepository;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;
@Controller
public class ProfileController {
@Autowired private UserRepository userRepo;
@Autowired private UserService userService;
@GetMapping("/profile")
public String getProfile(Model model, @AuthenticationPrincipal User user) {
    model.addAttribute("username", user.getUsername());
    return "profile";
}

/*@PostMapping("profile")
public String updateProfile(
        @AuthenticationPrincipal User user,
        @RequestParam String password,
        @RequestParam String email
) 
{
	userService.updateProfile(user, password, email);

    return "redirect:/user/profile";
}*/
}