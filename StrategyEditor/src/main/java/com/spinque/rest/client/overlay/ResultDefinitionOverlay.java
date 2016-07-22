package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class ResultDefinitionOverlay extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected ResultDefinitionOverlay() { }

	// Typically, methods on overlay types are JSNI
	public final native String getSourcePoint() /*-{ return this.sourcepoint; }-*/;

}
