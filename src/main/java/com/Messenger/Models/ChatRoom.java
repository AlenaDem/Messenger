package com.Messenger.Models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "chats")
public class ChatRoom {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
    @ManyToOne
	private ChatType type;
    
    @ManyToOne
    private User creator;

	@JsonIgnore
    @OneToMany(mappedBy = "chat")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ChatMessage> messages;
    
    @JsonIgnore
    @OneToMany(mappedBy = "chat")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<ChatUserRelation> chats;
    
    public Set<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(Set<ChatMessage> messages) {
		this.messages = messages;
	}

	public Set<ChatUserRelation> getChats() {
		return chats;
	}

	public void setChats(Set<ChatUserRelation> chats) {
		this.chats = chats;
	}

	public ChatRoom() {
    }

	public ChatRoom(String name, ChatType type, User creator) {
        this.name = name;
        this.type = type;
        this.creator = creator;
    }
    
    public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}
}
