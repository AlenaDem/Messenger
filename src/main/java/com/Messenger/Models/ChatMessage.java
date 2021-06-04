package com.Messenger.Models;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.Messenger.Settings;



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
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false, length=Settings.MAX_MESSAGE_LENGTH)
	private String text;
    
    @Column(nullable = false)
	private String attachedFile;
    
    @Lob
    @Column(nullable = false)
    private byte[] file;
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public ChatMessage() {
	}
	
	public ChatMessage(User user, ChatRoom chat, String text) {
		this.user = user;
		this.chat = chat;
		this.text = text;
	}

	public ChatMessage(Long id, User user, ChatRoom chat, String text) {
		this.id = id;
		this.user = user;
		this.chat = chat;
		this.text = text;
	}
    
	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAttachedFile() {
		return attachedFile;
	}

	public void setAttachedFile(String attachedFile) {
		this.attachedFile = attachedFile;
	}

}