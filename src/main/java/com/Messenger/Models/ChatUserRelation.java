package com.Messenger.Models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "chat_user_relations")
public class ChatUserRelation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @ManyToOne
	private User user;
    
	@ManyToOne
	private ChatRoom chat;

    public ChatUserRelation() {
    }
    
	public ChatUserRelation(ChatRoom chat, User user) {
		this.user = user;
		this.chat = chat;
	}

	public Long getId() {
		return id;
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
}
