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
//example http://localhost:8081/user?user=admin
@RequestMapping(value = "/user", method = RequestMethod.GET, params = "user")
public ModelAndView showProfileByUsername(HttpServletRequest request)
{
    ModelAndView model = new ModelAndView("user");
    User user = userRepo.findByUsername(request.getParameter("user"));
    model.addObject("username", user.getUsername()); 
    return model;
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

}
