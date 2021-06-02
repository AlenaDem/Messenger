package com.Messenger.Info;

import com.Messenger.Models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendInfo {
	@JsonProperty("id")	public Long userId;
	@JsonProperty("name") public String username;
	@JsonProperty("confirmed")	public boolean confirmed;
	
	public FriendInfo(User user) {
		userId = user.getId();
		username = user.getUsername();
		confirmed = false;
	}
}
