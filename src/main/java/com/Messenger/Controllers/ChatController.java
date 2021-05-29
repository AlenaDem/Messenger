package com.Messenger.Controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.ChatInfo;
import com.Messenger.MessageInfo;
import com.Messenger.Settings;
import com.Messenger.Models.ChatMessage;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatTypeRepository;
import com.Messenger.Repo.ChatUserRelationRepository;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;
import com.Messenger.Services.UserChatRelationService;
import com.Messenger.Services.UserService;


@RestController
public class ChatController {

	@Autowired private SimpMessagingTemplate smt;
	@Autowired private UserRepository userRepo;
	@Autowired private RoleRepository roleRepo;
	@Autowired private ChatRoomRepository chatRepo;
	@Autowired private ChatTypeRepository chatTypesRepo;
	@Autowired private ChatUserRelationRepository chatUserRepo;
	@Autowired private ChatMessageRepository msgRepo;
	@Autowired private UserService userService;
	@Autowired UserChatRelationService ucrService;
	
	@MessageMapping("/chat/message/{toChat}")
	public void sendMessage(@DestinationVariable Long toChat, @Payload Map message, Principal principal) {
		var username = principal.getName();
		var chat = chatRepo.findById(toChat).get();
		var user = userRepo.findByUsername(username);
		if (chat != null && user != null) {
			if (!ucrService.userInChat(username, toChat)) {
				System.out.println("Incorrect new message: " + message.get("message") + " to chat: " + toChat + " from " + principal.getName());
				return;
			}
			
			var inFiletype = message.get("file_type").toString();
			var inText= message.get("text").toString();
			var attachedFile = saveMessageFile(message);
			
			if (inText.length() > Settings.MAX_MESSAGE_LENGTH)
				return;
			
			System.out.println("New message: " + inText + " to chat " + toChat + " from " + principal.getName());
			
			ChatMessage msg = new ChatMessage();
			msg.setUser(user);
			msg.setChat(chat);
			msg.setText(inText);
			msg.setDate(new Date());
			msg.setType(inFiletype);
			msg.setAttachedFile(attachedFile);
			msgRepo.save(msg);
			
			smt.convertAndSend("/topic/chat/message/" + toChat, new MessageInfo(msg));
			System.out.println("Added to db and sent to subscribers");
		}
	}
	
	private String saveMessageFile(Map message) {
		var inFilename = message.get("file_name").toString();
		var inContent = message.get("file_content").toString();
		
		var splitted = inContent.split(",");
		if (splitted.length < 2)
			return "";
		
		var base64 = splitted[1];
		if (base64.isEmpty())
			return "";
		
	    File directory = new File(Settings.getFilesDir());
	    if (!directory.exists()){
	        directory.mkdir();
	    }
		
		DateFormat dateFormat = new SimpleDateFormat("yyyymmdd_hhmmss_");  
	    String savedFilename = dateFormat.format(new Date()) + inFilename;
		byte[] decodedContent = Base64.getDecoder().decode(base64);
		try (FileOutputStream fos = new FileOutputStream(Settings.getFilesDir() + savedFilename)) {
			fos.write(decodedContent);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return savedFilename;
	}
	
	@GetMapping("/fetchChats")
	public ArrayList<ChatRoom> fetchChats(Principal principal) {
		var chats = new ArrayList<ChatRoom>();
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return chats;
		
		for (var relation : user.getChats()) {
			chats.add(relation.getChat());
		}
		
		System.out.println("User chats: " + chats.size());
		
		return chats;	
	}
	
	@GetMapping("/fetchMessages/{chatId}")
	public ArrayList<MessageInfo> fetchMessages(@PathVariable("chatId") Long chatId, Principal principal) {
		var messages = new ArrayList<MessageInfo>();
		var chat = chatRepo.findById(chatId).get();
		if (chat == null)
			return messages;
		
		if (!ucrService.userInChat(principal.getName(), chatId))
			return messages;
		
		for (var chatMsg : msgRepo.findAllByChat(chat)) {
			messages.add(new MessageInfo(chatMsg));
		}
		
		System.out.println("Found %d messages for chat %s".formatted(messages.size(), chat.getName()));
		return messages;	
	}
	
	@GetMapping("/getChats/{name}")
	public ArrayList<ChatInfo> fetchChats(@PathVariable("name") String name, Principal principal) {
		var chats = new ArrayList<ChatInfo>();
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return chats;
		
		// if admin -> private
		
		for (var chat : chatRepo.findAll()) {
			var chatInfo = new ChatInfo(chat.getId(), chat.getName(), chat.getChats().size());
			if (!chat.getType().isPublic())
				continue;
			
			if (name.equals("*")) {
				chats.add(chatInfo);
				continue;
			}
			
			if (chat.getName().toLowerCase().contains(name))
				chats.add(chatInfo);
		}
		System.out.println("Chats contains %s in names: %d".formatted(name, chats.size()));
		return chats;	
	}
	
	@MessageMapping("/chat/create")
	public void create(@Payload Map request, Principal principal) {
		var type = chatTypesRepo.findByName(request.get("type").toString());
		if (type == null)
			return;
		
		var creator = userRepo.findByUsername(principal.getName());
		if (creator == null)
			return;
		
		ChatRoom chat = new ChatRoom(request.get("chatname").toString(), type);
		chatRepo.save(chat);
		
		var relation = new ChatUserRelation(chat, creator);
		chatUserRepo.save(relation);
		System.out.println("Relation added user: %s chatroom %s".formatted(creator.getUsername(), chat.getName()));
		smt.convertAndSend("/topic/chat/created/" + creator.getUsername(), true);
		
		if (type.isPersonal()) {
			var username = request.get("username").toString();
			if (!username.equals(creator.getUsername()) && userRepo.existsByUsername(username)) {
				var user = userRepo.findByUsername(username);
				relation = new ChatUserRelation(chat, user);
				chatUserRepo.save(relation);
				System.out.println("Relation added user: %s chatroom %s".formatted(username, chat.getName()));
				smt.convertAndSend("/topic/chat/created/" + username, true);
			}
		}
		
		System.out.println("Chat created: %s %s %s".formatted(chat.getId(), chat.getName(), chat.getType().getName()));
	}
	
	@MessageMapping("/chat/leave/{chatId}")
	public void leaveChat(@DestinationVariable Long chatId, Principal principal) {
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return;
		
		var chatOpt = chatRepo.findById(chatId);
		if (chatOpt.isEmpty())
			return;
		
		var chat = chatOpt.get();
		var rel = chatUserRepo.findByUserAndChat(user, chat);
		if (rel == null)
			return;

		chatUserRepo.delete(rel);
		smt.convertAndSend("/topic/chat/updated/" + user.getUsername(), true);
		System.out.println("User %s removed from chat %s".formatted(user.getUsername(), chat.getName()));
	}
	
	@MessageMapping("/chat/join/{chatId}")
	public void joinChat(@DestinationVariable Long chatId, Principal principal) {
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return;
		
		var chatOpt = chatRepo.findById(chatId);
		if (chatOpt.isEmpty())
			return;
		
		var chat = chatOpt.get();
		var rel = chatUserRepo.findByUserAndChat(user, chat);
		if (rel != null)
			return;
		
		rel = new ChatUserRelation(chat, user);
		chatUserRepo.save(rel);
		
		var response = Map.of("chat_id", chat.getId(), "chat_name", chat.getName());
		smt.convertAndSend("/topic/chat/joined/" + user.getUsername(), response);
		System.out.println("User %s joins to chat %s".formatted(user.getUsername(), chat.getName()));
	}
}
