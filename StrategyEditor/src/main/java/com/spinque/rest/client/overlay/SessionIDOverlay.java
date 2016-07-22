package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class SessionIDOverlay extends JavaScriptObject {

	protected SessionIDOverlay() { }
	
	public final native String getSessionID() /*-{ return this.sessionID; }-*/;
	
}
