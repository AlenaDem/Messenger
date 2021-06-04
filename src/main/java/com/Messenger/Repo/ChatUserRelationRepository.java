package com.Messenger.Repo;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Models.User;

public interface ChatUserRelationRepository extends CrudRepository<ChatUserRelation, Long> {
	ChatUserRelation findByUserAndChat(User user, ChatRoom chat);
	ArrayList<ChatUserRelation> findAllByChat(ChatRoom chat);
	@Transactional void deleteAllByChat(ChatRoom chat);
}