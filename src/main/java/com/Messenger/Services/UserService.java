package com.Messenger.Services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Messenger.Settings;
import com.Messenger.Models.ChatRoom;
import com.Messenger.Models.Role;
import com.Messenger.Models.User;
import com.Messenger.Repo.RoleRepository;
import com.Messenger.Repo.UserRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    
    public boolean saveUser(User user) {
    	var userRole = roleRepository.findByName(Settings.ROLE_USER);
    	if (userRole != null)
    		return saveUser(user, userRole);
    	return false;
    }
    
    public boolean saveUser(User user, Role role) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null)
            return false;
        
        userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null)
            return false;

        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
    
	public boolean userCanManageChat(User user, ChatRoom chat) {
		return chat.getCreator().getId() == user.getId() || user.getRole().isAdmin() || user.getRole().isSuperAdmin();
	}
}
