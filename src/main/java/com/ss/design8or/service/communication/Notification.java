package com.ss.design8or.service.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	
	private String title;
	
	private String body;
	
	private String icon;
	
	private NotificationData data;
}