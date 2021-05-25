package com.Messenger.Models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "chats")
public class ChatRoom {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
    @ManyToOne
	private ChatType type;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    private Set<ChatMessage> messages;
    
    public ChatRoom() {
    }

    public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}

	public ChatRoom(String name) {
        this.name = name;
    }

	public ChatRoom(String name, ChatType type) {
        this.name = name;
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
}
