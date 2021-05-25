package com.Messenger.Models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "chat_types")
public class ChatType {
    @Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private Set<ChatRoom> chats;

    public ChatType() {
    }

    public ChatType(String name) {
        this.name = name;
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