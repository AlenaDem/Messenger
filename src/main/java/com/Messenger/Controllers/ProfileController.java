package com.Messenger.Controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.Messenger.Settings;
import com.Messenger.Info.FriendInfo;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.FriendshipService;

@Controller
public class ProfileController {
	
	@Autowired UserRepository userRepo;
	@Autowired RoleRepository roleRepo;
	@Autowired FriendshipService friendService;
	
    @GetMapping("/profile")
    public String profile(Principal principal) {
		var currentUser = userRepo.findByUsername(principal.getName());
		if (currentUser != null)
			return "redirect:/profile/%s".formatted(currentUser.getUsername());
		return "redirect:/";
    }
	
    @GetMapping("/profile/{username}")
    public String userProfile(@PathVariable("username") String username, Model model, Principal principal) {
		var currentUser = userRepo.findByUsername(principal.getName());
		if (currentUser == null)
			return "redirect:/";
		
		var user = userRepo.findByUsername(username);
		if (user == null)
			return "redirect:/";
		
		model.addAttribute("username", user.getUsername());
		model.addAttribute("userid", user.getId());
		model.addAttribute("friend", friendService.isFriends(currentUser, user));
		model.addAttribute("avatar_source", user.getAvatar().length > 0 ? "/avatar/" + user.getUsername() : "/images/default_avatar.png");
		model.addAttribute("role_change_available", currentUser.getRole().isSuperAdmin() && !user.getRole().isSuperAdmin());
		model.addAttribute("ready_to_promote", !user.getRole().isAdmin());
		
		if (user.getId() == currentUser.getId()) {
			model.addAttribute("myprofile", true);
			model.addAttribute("email", user.getEmail());
		}
		else {
			model.addAttribute("myprofile", false);
		}
		
		return "profile";
    }
	
    @GetMapping("/avatar/{username}")
    public void getAvatar(@PathVariable("username") String username, HttpServletRequest request, HttpServletResponse response, Principal principal) 
    {
    	var user = userRepo.findByUsername(username);
		if (user == null)
			return;
    	
    	try {
    		response.getOutputStream().write(user.getAvatar());
    		response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Ошибка! Не удалось загрузить аватар пользователя.");
		}
    }
	
    @RequestMapping(value="/avatar", method=RequestMethod.POST)
	@ResponseBody
    public String setAvatar(@RequestParam("avatar") MultipartFile avatar, Model model, Principal principal){
        if (!avatar.isEmpty()) {
            try {
                byte[] bytes = avatar.getBytes();
            	var user = userRepo.findByUsername(principal.getName());
        		if (user != null) {
	        		user.setAvatar(bytes);
	        		userRepo.save(user);
	        		System.out.println("Аватар загружен");
	                model.addAttribute("user", user);
	                return "profile";
        		}

            } catch (Exception e) {
                System.out.println("Не удалось загрузить аватар." + e.getMessage());
            }
        }
    	return "redirect:/";
    }
    
	@GetMapping("/fetchFriends")
	@ResponseBody 
	public ArrayList<FriendInfo> fetchFriends(Principal principal) {
		var friends = new ArrayList<FriendInfo>();
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return friends;
		
		for (var pendFriend : friendService.invites(user))
			friends.add(new FriendInfo(pendFriend));
		
		for (var confFriend : friendService.confirmedFriends(user)) {
			var info = new FriendInfo(confFriend);
			info.confirmed = true;
			friends.add(info);
		}
		
		System.out.println("Found %d friends for %s".formatted(friends.size(), user.getUsername()));
		return friends;
	}
	
    @PostMapping("/change_role")
    @ResponseBody
    public void promote(@RequestParam("user_id") Long userId, @RequestParam("promote") boolean isPromote, Principal principal) {  	
		var currentUser = userRepo.findByUsername(principal.getName());
		if (currentUser == null || !currentUser.getRole().isSuperAdmin())
			return;
		
		if (!userRepo.existsById(userId))
			return;
		var user = userRepo.findById(userId).get();
		
		if (isPromote) {
			if (user.getRole().isSuperAdmin() || user.getRole().isAdmin())
				return;
			
			var adminRole = roleRepo.findByName(Settings.ROLE_ADMIN);
			if (adminRole == null)
				return;
			user.setRole(adminRole);
			userRepo.save(user);
	        System.out.println("User %s promoted to admin by %s".formatted(user.getUsername(), currentUser.getUsername()));
		} 
		else {
			var userRole = roleRepo.findByName(Settings.ROLE_USER);
			if (userRole == null)
				return;
			user.setRole(userRole);
			userRepo.save(user);
			System.out.println("User %s demoted to user by %s".formatted(user.getUsername(), currentUser.getUsername()));
		}
    }
	
    @PostMapping("/addfriend/{userId}")
    @ResponseBody
    public void addFriend(@PathVariable("userId") Long userId, Model model, Principal principal) {
		var fromUser = userRepo.findByUsername(principal.getName());
		if (fromUser == null)
			return;
		
		if (!userRepo.existsById(userId))
			return;
		var toUser = userRepo.findById(userId).get();
		
		friendService.addFriend(fromUser, toUser);
        System.out.println("%s sent friend invite to %s".formatted(fromUser.getUsername(), toUser.getUsername()));
    }
    
    @PostMapping("/removefriend/{userId}")
    @ResponseBody
    public void removeFriend(@PathVariable("userId") Long userId, Model model, Principal principal) {
		var fromUser = userRepo.findByUsername(principal.getName());
		if (fromUser == null)
			return;
		
		if (!userRepo.existsById(userId))
			return;
		var toUser = userRepo.findById(userId).get();
		
		friendService.removeFriend(fromUser, toUser);
        System.out.println("%s removed %s from friends".formatted(fromUser.getUsername(), toUser.getUsername()));
    }
    
    @PostMapping("/confirmfriend/{userId}")
    @ResponseBody
    public void confirmFriend(@PathVariable("userId") Long userId, Model model, Principal principal) {
		var toUser = userRepo.findByUsername(principal.getName());
		if (toUser == null)
			return;
		
		if (!userRepo.existsById(userId))
			return;
		var fromUser = userRepo.findById(userId).get();
		
		friendService.confirm(fromUser, toUser);
        System.out.println("%s confirmed friend invite from %s".formatted(toUser.getUsername(), fromUser.getUsername()));
    }
}
