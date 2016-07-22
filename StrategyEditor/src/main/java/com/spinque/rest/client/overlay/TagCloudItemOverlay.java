package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class TagCloudItemOverlay extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected TagCloudItemOverlay() { }
	  
	// Typically, methods on overlay types are JSNI
	public final native String getName() /*-{ return this.word; }-*/;
	public final native double getScore() /*-{ return this.score; }-*/;
}
