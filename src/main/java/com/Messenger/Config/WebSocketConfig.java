package com.Messenger.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private String url = "http://localhost:8081";
	
	@Bean
	public TopicSubscriptionInterceptor topicSubscriptionInterceptor() {
	    return new TopicSubscriptionInterceptor();
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/msg").setAllowedOrigins(url).withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
	}
	
	public void configureClientInboundChannel(ChannelRegistration registration) {
	    registration.interceptors(topicSubscriptionInterceptor());
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registry);
		registry.setMessageSizeLimit((512 + 1024) * 1024);
		registry.setSendTimeLimit(20 * 10000);
		registry.setSendBufferSizeLimit((512 + 1024) * 1024);
	}
	
	

}
