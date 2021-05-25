package com.Messenger.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Models.ChatMessage;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.User;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatTypeRepository;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserService;

@RestController
public class ChatController {

	@Autowired
	private SimpMessagingTemplate smt;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private ChatRoomRepository chatsRepo;
	
	@Autowired
	private ChatTypeRepository chatTypesRepo;
	
	@Autowired
	private ChatMessageRepository msgRepo;

	@Autowired
	private UserService userService;

	@MessageMapping("/chat/{toChat}")
	public void sendMessage(@DestinationVariable Long toChat, @Payload Map message, Principal principal) {
		System.out.println("get message: " + message.get("message") + " to chat: " + toChat + " from " + principal.getName());
		
		var chat = chatsRepo.findById(toChat).get();
		var user = userRepo.findByUsername(principal.getName());
		if (chat != null && user != null) {
			var messageContent = message.get("message").toString();
			ChatMessage msg = new ChatMessage(user, chat, messageContent);
			msgRepo.save(msg);
			var map = Map.of("chat_id", chat.getId(), "sender", user.getUsername(), "message", messageContent);
			smt.convertAndSend("/topic/messages/" + toChat, map);
			System.out.println("Added to db and sent to subscribers");
		}
	}

	@GetMapping("/fetchChats")
	public ArrayList<ChatRoom> fetchChats(Principal principal) {
		var rooms = new ArrayList<ChatRoom>();
		for (ChatRoom r : chatsRepo.findAll())
			rooms.add(r);
		return rooms;
	}
}
