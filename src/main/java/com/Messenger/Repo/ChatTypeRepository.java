package com.Messenger.Repo;

import org.springframework.data.repository.CrudRepository;
import com.Messenger.Models.ChatType;
import com.Messenger.Models.User;

public interface ChatTypeRepository extends CrudRepository<ChatType, Long> {
	ChatType findByName(String name);
}