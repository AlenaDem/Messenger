package com.Messenger.Repo;

import org.springframework.data.repository.CrudRepository;

import com.Messenger.Models.ChatMessage;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
}