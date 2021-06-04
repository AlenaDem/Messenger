package com.Messenger.Repo;

import org.springframework.data.repository.CrudRepository;

import com.Messenger.Models.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Role findByName(String name);
}