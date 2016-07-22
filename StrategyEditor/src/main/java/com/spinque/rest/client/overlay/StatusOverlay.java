package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class StatusOverlay extends JavaScriptObject {
	
	public final static String OK = "OK";
	public final static String SUGGESTIONS = "SUGGESTIONS";
	public final static String PREMATURE = "PREMATURE";
	public final static String ERROR = "ERROR";
	public final static String WARNING = "WARNING";
	
	// Overlay types always have protected, zero-arg ctors
	protected StatusOverlay() { }
	  
	// Typically, methods on overlay types are JSNI
	// Either:
	// 
	// OK : all is fine 
	// SUGGESTIONS : the strartegy is complete, but it probably could be better
	// PREMATURE : the strategy looks not yet complete; suggestions are given 
	// ERROR : code is not compilable
	public final native String getStatus() /*-{ return this.status; }-*/;
	
	/* optional */
	public final native JsArray<MessageOverlay> getMessages() /*-{ return this.messages; }-*/;

	public static final native StatusOverlay create(String status, MessageOverlay message) /*-{ 
		return { 
			"status": status, 
			"messages" : [ message ] 
		}; 
	}-*/;
	public static final native StatusOverlay create(String status) /*-{ 
		return { 
			"status": status, 
			"messages" : [ ] 
		}; 
	}-*/;
}