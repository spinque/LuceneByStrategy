package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class ParamOptionsOverlay extends JavaScriptObject {
	
	// Overlay types always have protected, zero-arg ctors
	protected ParamOptionsOverlay() { }
	  
	public final native boolean isOptionsKnown() /*-{ return this.known; }-*/;
	
	// Typically, methods on overlay types are JSNI
	public final native JsArrayString getOptions() /*-{ return this.options; }-*/;
}
