package com.Messenger;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatInfo {
	@JsonProperty("id")	public Long chatId;
	@JsonProperty("name") public String chatName;
	@JsonProperty("user_count")	public int userCount;
	
	public ChatInfo() {
	}
	
	public ChatInfo(Long chatId, String chatName, int userCount) {
		this.chatId = chatId;
		this.chatName = chatName;
		this.userCount = userCount;
	}
}
