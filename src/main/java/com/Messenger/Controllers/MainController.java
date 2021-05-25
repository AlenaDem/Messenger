package com.Messenger.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatType;
import com.Messenger.Models.Role;
import com.Messenger.Models.User;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatTypeRepository;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;

@Controller
public class MainController {

	@Autowired
	private UserRepository ur;

	@Autowired
	private RoleRepository rr;
	
	@Autowired
	private ChatRoomRepository crr;
	
	@Autowired
	private ChatTypeRepository ctr;

	@Autowired
	private UserService userService;

	@RequestMapping("/users")
	public String users(Model model) {
		var users = ur.findAll();
		model.addAttribute("users", users);
		return "/login";
	}

	@RequestMapping("/")
	public String index() {
		return "/index";
	}

	@RequestMapping("/demo")
	public String demo() {
		System.out.println("Demo create");
		Role role = new Role("ROLE_USER");
		rr.save(role);
		var user = new User("user", "1", role);
		userService.saveUser(user, role);

		role = new Role("ROLE_ADMIN");
		rr.save(role);
		user = new User("admin", "1", role);
		userService.saveUser(user, role);
		
		var chatType = new ChatType("PUBLIC");
		ctr.save(chatType);
		
		var chatRoom = new ChatRoom("Комната 123", chatType);
		crr.save(chatRoom);

		return "redirect:/";
	}

	@RequestMapping("/chat")
	public String chat() {
		return "/chat";
	}

}