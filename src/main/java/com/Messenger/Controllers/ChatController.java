package com.Messenger.Controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Settings;
import com.Messenger.Info.ChatInfo;
import com.Messenger.Info.MessageInfo;
import com.Messenger.Info.UserInfo;
import com.Messenger.Models.ChatMessage;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Models.User;
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

	@Autowired SimpMessagingTemplate smt;
	@Autowired UserRepository userRepo;
	@Autowired RoleRepository roleRepo;
	@Autowired ChatRoomRepository chatRepo;
	@Autowired ChatTypeRepository chatTypesRepo;
	@Autowired ChatUserRelationRepository chatUserRepo;
	@Autowired ChatMessageRepository msgRepo;
	@Autowired UserService userService;
	@Autowired UserChatRelationService ucrService;
	
	@MessageMapping("/chat/message/new/{chatId}")
	public void sendMessage(@DestinationVariable Long chatId, @Payload Map message, Principal principal) {
		var username = principal.getName();
		
		if (!chatRepo.existsById(chatId))
			return;
		var chat = chatRepo.findById(chatId).get();
		
		var user = userRepo.findByUsername(username);
		if (user == null)
			return;
		
		if (!ucrService.userInChat(username, chatId))
			return;
		
		var inText= message.get("text").toString();
		if (inText.length() > Settings.MAX_MESSAGE_LENGTH)
			return;
		
		System.out.println("New message: " + inText + " to chat " + chatId + " from " + principal.getName());
		var inFiletype = message.get("file_type").toString();
		var inContent = message.get("file_content").toString();
		var inFilename = message.get("file_name").toString();
		
		ChatMessage msg = new ChatMessage();
		msg.setUser(user);
		msg.setChat(chat);
		msg.setText(inText);
		msg.setDate(new Date());
		msg.setType(inFiletype);
		msg.setAttachedFile(inFilename);
		msg.setFile(decodeFile(inContent));
		msgRepo.save(msg);
		
		var response = new MessageInfo(msg);
		response.deletable = userService.userCanManageChat(user, chat) || msg.getUser().getId() == user.getId();
		
		smt.convertAndSend("/topic/chat/message/created/" + chatId, response);
		System.out.println("New message %d from %s added to db and sent to subscribers".formatted(msg.getId(), user.getUsername()));
	}
	
	@MessageMapping("/chat/message/delete")
	public void deleteMessage(@Payload Map message, Principal principal) {
		var username = principal.getName();
		var chatId = Long.valueOf(message.get("chat_id").toString());
		var msgId = Long.valueOf(message.get("message_id").toString());
		
		if (!msgRepo.existsById(msgId))
			return;
		var msg = msgRepo.findById(msgId).get();
		
		if (!chatRepo.existsById(chatId))
			return;
		var chat = chatRepo.findById(chatId).get();
		
		var user = userRepo.findByUsername(username);
		if (user == null)
			return;
		
		if (!ucrService.userInChat(username, chatId))
			return;
		
		if (userService.userCanManageChat(user, chat) ||  msg.getUser().getId() == user.getId()) {
			msgRepo.delete(msg);
			smt.convertAndSend("/topic/chat/message/deleted/" + chatId, chatId);
			System.out.println("Message %d from chat %s was deleted by %s".formatted(msg.getId(), chat.getName(), user.getUsername()));
		}
	}
	
	private byte[] decodeFile(String base64string) {
		
		var result = new byte[0];

		var splitted = base64string.split(",");
		if (splitted.length < 2)
			return result;
		
		var base64 = splitted[1];
		if (base64.isEmpty())
			return result;
		
		byte[] decodedContent = Base64.getDecoder().decode(base64);
		return decodedContent;
	}
	
	@GetMapping("/fetchChats")
	public ArrayList<ChatRoom> fetchChats(Principal principal) {
		var chats = new ArrayList<ChatRoom>();
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return chats;
		
		for (var relation : user.getChats())
			chats.add(relation.getChat());
		
		System.out.println("User chats count: " + chats.size());
		return chats;	
	}
	
	@GetMapping("/fetchMessages/{chatId}")
	public ArrayList<MessageInfo> fetchMessages(@PathVariable("chatId") Long chatId, Principal principal) {
		var messages = new ArrayList<MessageInfo>();
		
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return messages;
		
		if (!chatRepo.existsById(chatId))
			return messages;
		var chat = chatRepo.findById(chatId).get();
		
		if (!ucrService.userInChat(principal.getName(), chatId))
			return messages;
		
		for (var chatMsg : msgRepo.findAllByChat(chat)) {
			var info = new MessageInfo(chatMsg);
			info.deletable = userService.userCanManageChat(user, chat) || chatMsg.getUser().getId() == user.getId();
			messages.add(info);
		}
		
		System.out.println("Found %d messages for chat %s".formatted(messages.size(), chat.getName()));
		return messages;	
	}
	
	@GetMapping("/fetchUsers/{chatId}")
	public ArrayList<UserInfo> fetchUsers(@PathVariable("chatId") Long chatId, Principal principal) {
		var users = new ArrayList<UserInfo>();
		
		if (!chatRepo.existsById(chatId))
			return users;
		var chat = chatRepo.findById(chatId).get();
		
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return users;
		
		if (!ucrService.userInChat(principal.getName(), chatId))
			return users;
		
		for (var rel : chatUserRepo.findAllByChat(chat)) {
			var userInfo = new UserInfo();
			var chatUser = rel.getUser();
			userInfo.id = chatUser.getId();
			userInfo.username = chatUser.getUsername();
			userInfo.managable = userService.userCanManageChat(user, chat) || chatUser.getUsername().equals(user.getUsername());
			users.add(userInfo);
		}
		
		System.out.println("Found %d users for chat %s".formatted(users.size(), chat.getName()));
		return users;	
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
		
		ChatRoom chat = new ChatRoom(request.get("chatname").toString(), type, creator);
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
		smt.convertAndSend("/topic/chat/leave/" + user.getUsername(), chatId);
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
	
	@MessageMapping("/chat/kick")
	public void kickUser(@Payload Map message, Principal principal) {
		var chatId = Long.valueOf(message.get("chat_id").toString());
		var userId = Long.valueOf(message.get("user_id").toString());
		
		if (!chatRepo.existsById(chatId))
			return;
		var chat = chatRepo.findById(chatId).get();
		
		var initiator = userRepo.findByUsername(principal.getName());
		if (initiator == null)
			return;
		
		if (!userRepo.existsById(userId))
			return;
		var user = userRepo.findById(userId).get();
		
		if (!ucrService.userInChat(initiator.getUsername(), chatId))
			return;
		
		if (!ucrService.userInChat(user.getUsername(), chatId))
			return;
		
		if (userService.userCanManageChat(initiator, chat) || user.getUsername().equals(initiator.getUsername())) {
			var rel = chatUserRepo.findByUserAndChat(user, chat);
			if (rel == null)
				return;
			chatUserRepo.delete(rel);
			smt.convertAndSend("/topic/chat/leave/" + user.getUsername(), chatId);
			smt.convertAndSend("/topic/chat/kicked/" + initiator.getUsername(), chatId);
			System.out.println("User %s kicked user %s from chat %s".formatted(initiator.getUsername(), user.getUsername(), chat.getName()));
		}
	}
	
	@MessageMapping("/chat/add")
	public void addUser(@Payload Map message, Principal principal) {
		var chatId = Long.valueOf(message.get("chat_id").toString());
		var userId = Long.valueOf(message.get("user_id").toString());
		
		if (!chatRepo.existsById(chatId))
			return;
		var chat = chatRepo.findById(chatId).get();
		
		if (!userRepo.existsById(userId))
			return;
		var user = userRepo.findById(userId).get();
		
		if (ucrService.userInChat(user.getUsername(), chatId))
			return;
		
		var rel = new ChatUserRelation(chat, user);
		chatUserRepo.save(rel);
		
		var response = Map.of("chat_id", chat.getId(), "chat_name", chat.getName());
		smt.convertAndSend("/topic/chat/created/" + user.getUsername(), response);
		System.out.println("User %s added to chat %s by %s".formatted(user.getUsername(), chat.getName(), principal.getName()));
	}
	
	@MessageMapping("/chat/delete/{chatId}")
	public void deleteChat(@DestinationVariable Long chatId, Principal principal) {
		if (!chatRepo.existsById(chatId))
			return;
		var chat = chatRepo.findById(chatId).get();
		
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return;
		
		if (!ucrService.userInChat(user.getUsername(), chatId))
			return;
		
		if (!userService.userCanManageChat(user, chat))
			return;
		
		chatRepo.delete(chat);
		var response = Map.of("chat_id", chat.getId());
		smt.convertAndSend("/topic/chat/deleted/" + chat.getId(), response);
		System.out.println("User %s deleted chat %s".formatted(user.getUsername(), chat.getName()));
	}
	
	@GetMapping("/fetchChatSettings/{chatId}")
	public HashMap<String, Object> fetchChatSettings(@PathVariable Long chatId, Principal principal) {
		var response = new HashMap<String, Object>();
		response.put("can_delete", false);
		
		if (!chatRepo.existsById(chatId))
			return response;
		var chat = chatRepo.findById(chatId).get();
		
		var user = userRepo.findByUsername(principal.getName());
		if (user == null)
			return response;
		
		if (ucrService.userInChat(user.getUsername(), chatId))
			return response;
		
		response.put("can_delete", userService.userCanManageChat(user, chat));
		return response;
	}
}
