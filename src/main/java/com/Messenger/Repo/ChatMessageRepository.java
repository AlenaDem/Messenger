package com.Messenger.Repo;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.Messenger.Models.ChatMessage;
import com.Messenger.Models.ChatRoom;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
	ArrayList<ChatMessage> findAllByChat(ChatRoom chat);
}