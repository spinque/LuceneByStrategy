package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class MessageOverlay extends JavaScriptObject {
	
	// Overlay types always have protected, zero-arg ctors
	protected MessageOverlay() { }
	
	// Typically, methods on overlay types are JSNI
	// Either: INFO, WARNING, ERROR, SUGGESTIONS
	public final native String getSeverity() /*-{ return this.severity; }-*/;
	
	// description of what is wrong, or what is good
	public final native String getMessage() /*-{ return this.message; }-*/;

	public final native static MessageOverlay create(String message, String severity) /*-{ 
		return { 
			"message": message,
			"severity": severity
		}; 
	}-*/;


}
