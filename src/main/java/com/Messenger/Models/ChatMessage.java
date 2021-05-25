package com.Messenger.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class ChatMessage {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private ChatRoom chat;
    
	private String content;

	public Long getId() {
		return id;
	}
	
	public ChatMessage() {
	}
	
	public ChatMessage(User user, ChatRoom chat, String content) {
		this.user = user;
		this.chat = chat;
		this.content = content;
	}

	public ChatMessage(Long id, User user, ChatRoom chat, String content) {
		this.id = id;
		this.user = user;
		this.chat = chat;
		this.content = content;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ChatRoom getChat() {
		return chat;
	}

	public void setChat(ChatRoom chat) {
		this.chat = chat;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}