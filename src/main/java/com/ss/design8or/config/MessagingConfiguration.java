package com.ss.design8or.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author ezerbo
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class MessagingConfiguration implements WebSocketMessageBrokerConfigurer  {
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(Constants.WS_BROKER_DESTINATION_PREFIX)
        	.enableSimpleBroker(Constants.DESIGNATION_NSCHANNEL, Constants.POOL_NS_CHANNEL, Constants.ASSIGNMENT_NSCHANNEL);
    }
	
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(Constants.WS_ENDPOINT)
        .setAllowedOrigins("*")
        .withSockJS();
    }
    
}