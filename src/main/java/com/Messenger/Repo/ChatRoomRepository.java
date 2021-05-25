package com.Messenger.Repo;

import org.springframework.data.repository.CrudRepository;
import com.Messenger.Models.ChatRoom;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {
	ChatRoom findByName(String name);
}