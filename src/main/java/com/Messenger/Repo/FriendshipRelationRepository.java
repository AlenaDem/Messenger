package com.Messenger.Repo;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;

import com.Messenger.Models.FriendshipRelation;
import com.Messenger.Models.User;

public interface FriendshipRelationRepository extends CrudRepository<FriendshipRelation, Long> {
	ArrayList<FriendshipRelation> findAllByFromUserAndConfirmed(User fromUser, boolean confirmed);
	ArrayList<FriendshipRelation> findAllByToUserAndConfirmed(User toUser, boolean confirmed);
	FriendshipRelation findByFromUserAndToUser(User from, User to);
}
