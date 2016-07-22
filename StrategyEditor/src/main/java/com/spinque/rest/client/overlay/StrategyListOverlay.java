package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class StrategyListOverlay  extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected StrategyListOverlay() { }

	public final native JsArray<StrategyListItemOverlay> getItems() /*-{ return this.strategies; }-*/;
}
