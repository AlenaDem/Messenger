package com.Messenger.Controllers;

/*import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Messenger.Models.FriendForm;
import com.Messenger.Models.Role;
import com.Messenger.Models.User;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;
@Controller
public class FriendController {
	@Autowired private UserRepository userRepo;
	@Autowired private UserService userService;
	 
    @GetMapping("/friend")
	    String friends(Model model, @AuthenticationPrincipal User user) { 
	        FriendForm friendForm = new FriendForm();
	        friendForm.setUserId(user.getId());
	        model.addAttribute("friendForm", friendForm);
	        return "friend";
	    }
	 
	    @GetMapping("/friend/search")
	    String searchFriends(@Valid @ModelAttribute("friendForm") FriendForm friendForm, BindingResult result,
	            Model model) {
	        if (!result.hasErrors()) {
	            model.addAttribute("users", userService.searchFriend(friendForm));
	        }
	        return "friend";
	    }
	 
	    @PostMapping("/friend/add")
	    String addFriend(@RequestParam("userId") Long userId, Model model) {
	        userService.addFriend(userId);
	        return "redirect:/";
	    }
	 
	    @PostMapping("/friend/remove")
	    String removeFriend(@RequestParam("userId") Long userId, Model model) {
	        userService.removeFriend(userId);
	        return "redirect:/";
	    }
	}
*/

