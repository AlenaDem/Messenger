package com.Messenger.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
    @Transient
    private String passwordConfirm;
    
	@Column(nullable = false)
	private String email;
	
    @ManyToOne
    private Role role;
    
	@Column(nullable = false)
	private boolean confirmed;
	
    @Lob
    @Column(nullable = false)
    private byte[] avatar;

	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<ChatMessage> messages;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<ChatUserRelation> chats;
    
    @JsonIgnore
    @OneToMany(mappedBy = "creator")
    Set<ChatRoom> createdRooms;
    
    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    Set<FriendshipRelation> outFriends;
    
    @JsonIgnore
    @OneToMany(mappedBy = "toUser")
    Set<FriendshipRelation> inFriends;
    
	public User() {
		confirmed = false;
		avatar = new byte[0];
	}

	public Set<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(Set<ChatMessage> messages) {
		this.messages = messages;
	}

	public Set<ChatUserRelation> getChats() {
		return chats;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public void setChats(Set<ChatUserRelation> chats) {
		this.chats = chats;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public Set<ChatRoom> getCreatedRooms() {
		return createdRooms;
	}

	public void setCreatedRooms(Set<ChatRoom> createdRooms) {
		this.createdRooms = createdRooms;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return confirmed;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		var res = new ArrayList<Role>();
		res.add(role);
		return res;
	}
	
    public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

}
