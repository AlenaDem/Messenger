package com.Messenger.Services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Messenger.Models.FriendshipRelation;
import com.Messenger.Models.User;
import com.Messenger.Repo.ChatRoomRepository;
import com.Messenger.Repo.ChatUserRelationRepository;
import com.Messenger.Repo.FriendshipRelationRepository;
import com.Messenger.Repo.UserRepository;

@Service
public class FriendshipService {

	@Autowired UserRepository userRepo;
	@Autowired ChatRoomRepository chatRepo;
	@Autowired ChatUserRelationRepository chatUserRepo;
	@Autowired FriendshipRelationRepository friendRepo;
	
	public boolean isFriends(User user1, User user2) {
		var rel = friendRepo.findByFromUserAndToUser(user1, user2);
		if (rel != null)
			return true;
		rel = friendRepo.findByFromUserAndToUser(user2, user1);
		if (rel != null)
			return true;
		return false;
	}
	
	public ArrayList<User> confirmedFriends(User user) {
		var confirmedFriends = new ArrayList<User>();
		if (user == null)
			return confirmedFriends;
		
		for (var rel : friendRepo.findAllByFromUserAndConfirmed(user, true))
			confirmedFriends.add(rel.getToUser());
		for (var rel : friendRepo.findAllByToUserAndConfirmed(user, true))
			confirmedFriends.add(rel.getFromUser());
		
		return confirmedFriends;
	}
	
	public ArrayList<User> invites(User user) {
		var invites = new ArrayList<User>();
		if (user == null)
			return invites;
		for (var rel : friendRepo.findAllByToUserAndConfirmed(user, false))
			invites.add(rel.getFromUser());
		return invites;
	}
	
	public boolean addFriend(User from, User to) {
		if (isFriends(from, to))
			return false;
		
		var rel = new FriendshipRelation();
		rel.setFromUser(from);
		rel.setToUser(to);
		friendRepo.save(rel);
		return true;
	}
	
	public boolean removeFriend(User from, User to) {
		var rel = friendRepo.findByFromUserAndToUser(to, from);
		if (rel != null)
			friendRepo.delete(rel);
		
		rel = friendRepo.findByFromUserAndToUser(from, to);
		if (rel != null)
			friendRepo.delete(rel);
		
		return true;
	}
	
	public boolean confirm(User from, User to) {
		var rel = friendRepo.findByFromUserAndToUser(from, to);
		if (rel == null)
			return false;
		rel.setConfirmed(true);
		friendRepo.save(rel);
		return true;
	}
}