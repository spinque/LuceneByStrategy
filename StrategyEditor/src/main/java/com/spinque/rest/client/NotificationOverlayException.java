package com.spinque.rest.client;

import com.spinque.rest.client.overlay.NotificationOverlay;

public class NotificationOverlayException extends Exception {

	private static final long serialVersionUID = 5203074173307099029L;

	protected static final NotificationOverlayException NO_RESPONSE = new NotificationOverlayException("no response");

	protected static final NotificationOverlayException INVALID_RESPONSE = new NotificationOverlayException("invalid response");

	private final NotificationOverlay _notification;

	public NotificationOverlayException(NotificationOverlay notification) {
		_notification = notification;
	}
	
	public NotificationOverlayException(String message) {
		super(message);
		_notification = null;
	}

	@Override
	public String getMessage() {
		return _notification != null ? _notification.getMessage() : super.getMessage();
	}

	public NotificationOverlay getNotification() {
		return _notification;
	}
}
