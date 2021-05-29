package com.Messenger.Config;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import com.Messenger.Services.UserChatRelationService;

public class TopicSubscriptionInterceptor implements ChannelInterceptor {
	
	@Autowired UserChatRelationService ucrService;
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
	    if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
	        Principal userPrincipal = headerAccessor.getUser();
	        if(!validateSubscription(userPrincipal, headerAccessor.getDestination()))
	        {
	            throw new IllegalArgumentException("No permission for this topic");
	        }
			System.out.println("Interceptor: %s subscribes to %s".formatted(userPrincipal.getName(), headerAccessor.getDestination()));
	    }
		return message;
	}

	private boolean validateSubscription(Principal principal, String topicDestination)
	{
	    if (principal == null)
	        return false;
		
		if (topicDestination.startsWith("/topic/chat/message/")) {
			var chatId = getChatIdFromDestination(topicDestination);
			if (!ucrService.userInChat(principal.getName(), chatId))
				return false;
		}
	    return true;
	}
	
	private Long getChatIdFromDestination(String destination) {
		var splitted = destination.split("/");
		if (splitted.length < 5)
			return 0L;
		Long id = Long.parseLong(splitted[4]);
		return id;
	}
}
