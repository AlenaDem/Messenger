package com.Messenger.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatUserRelationRepository;
import com.Messenger.Repo.UserRepository;

@Service
public class UserChatRelationService {

	@Autowired UserRepository userRepo;
	@Autowired ChatRoomRepository chatRepo;
	@Autowired ChatUserRelationRepository chatUserRepo;
	
	public UserChatRelationService() {
	}
	
	public boolean userInChat(String username, Long chatId) {
		var user = userRepo.findByUsername(username);
		if (user == null)
			return false;
		
		if (!chatRepo.existsById(chatId))
			return false;
		var chat = chatRepo.findById(chatId).get();
		
		var rel = chatUserRepo.findByUserAndChat(user, chat);
		if (rel == null)
			return false;
		
		return true;
	}
}
