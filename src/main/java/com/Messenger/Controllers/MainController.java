package com.Messenger.Controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.Messenger.Settings;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatType;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Models.Role;
import com.Messenger.Models.User;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatTypeRepository;
import com.Messenger.Repo.ChatUserRelationRepository;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;

@Controller
public class MainController {

	@Autowired UserRepository userRepo;
	@Autowired RoleRepository roleRepo;
	@Autowired ChatRoomRepository chatRepo;
	@Autowired ChatTypeRepository chatTypeRepo;
	@Autowired UserService userService;
	@Autowired ChatUserRelationRepository chatUserRepo;

	@RequestMapping("/")
	public String index() {
		return "redirect:/chat";
	}

	@RequestMapping("/demo")
	public String demo() {

		Role role = new Role(Settings.ROLE_USER);
		roleRepo.save(role);
		var user = new User();
		user.setUsername("user");
		user.setPassword("1");
		user.setRole(role);
		user.setEmail("user@mail.xx");
		user.setConfirmed(true);
		userService.saveUser(user, role);

		role = new Role(Settings.ROLE_ADMIN);
		roleRepo.save(role);
		user = new User();
		user.setUsername("admin");
		user.setPassword("1");
		user.setRole(role);
		user.setEmail("admin@mail.xx");
		user.setConfirmed(true);
		userService.saveUser(user, role);
		
		role = new Role(Settings.ROLE_SUPERADMIN);
		roleRepo.save(role);
		user = new User();
		user.setUsername("sadmin");
		user.setPassword("1");
		user.setRole(role);
		user.setEmail("sadmin@mail.xx");
		user.setConfirmed(true);
		userService.saveUser(user, role);
		
		var chatType = new ChatType("PERSONAL");
		chatTypeRepo.save(chatType);
		chatType = new ChatType("PRIVATE");
		chatTypeRepo.save(chatType);
		chatType = new ChatType("PUBLIC");
		chatTypeRepo.save(chatType);
		var chatRoom = new ChatRoom("Комната Чипсика", chatType, user);
		chatRepo.save(chatRoom);
		
		var rel = new ChatUserRelation(chatRoom, user);
		chatUserRepo.save(rel);

		System.out.println("Demo db created");
		return "redirect:/";
	}

	@RequestMapping("/chat")
	public String chat() {
		return "chat";
	}
	
	@RequestMapping("/profile")
	public String profile() {
		return "profile";
	}
	
	@RequestMapping("/friends")
	public String friends() {
		return "friends";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		return "logout";
	}
}