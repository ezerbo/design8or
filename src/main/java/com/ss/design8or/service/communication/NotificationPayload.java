package com.ss.design8or.service.communication;

import lombok.Builder;
import lombok.Data;

/**
 * @author ezerbo
 *
 */
@Data
@Builder
public class NotificationPayload {

	private Notification notification;
}
