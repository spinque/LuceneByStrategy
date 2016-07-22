package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class NotificationOverlay extends JavaScriptObject {
	
	public static final String OK = "OK";

	// Overlay types always have protected, zero-arg ctors
	protected NotificationOverlay() { }
	  
	// Typically, methods on overlay types are JSNI
	public final native String getStatus() /*-{ return this.status; }-*/;
	public final native String getMessage() /*-{ return this.message; }-*/;
	
	public final native static NotificationOverlay create() /*-{ return { "status": "OK" }; }-*/;
	
	
	// only set in case of error
	public final native JsArray<StackTraceOverlay> getTrace() /*-{ return this.stacktrace; }-*/;
	
	public static final class StackTraceOverlay extends JavaScriptObject {
		protected StackTraceOverlay() { }
		public final native String getClassName() /*-{ return this.className; }-*/;
		public final native String getMessage() /*-{ return this.message; }-*/;
		public final native JsArrayString getStack() /*-{ return this.stack; }-*/;
	}
	
	/* optional */
	public final native JavaScriptObject getData() /*-{ return this.data; }-*/;

	public final native static NotificationOverlay create(String status, String message) /*-{ return { "status": status, "message": message }; }-*/;
}
