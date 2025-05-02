package com.ss.design8or.service.notification;

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
