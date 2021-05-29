package com.Messenger.Repo;

import org.springframework.data.repository.CrudRepository;

import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.ChatUserRelation;
import com.Messenger.Models.User;

public interface ChatUserRelationRepository extends CrudRepository<ChatUserRelation, Long> {
	ChatUserRelation findByUserAndChat(User user, ChatRoom chat);
}