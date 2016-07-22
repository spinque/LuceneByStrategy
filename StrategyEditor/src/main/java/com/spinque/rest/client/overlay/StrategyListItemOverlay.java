package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class StrategyListItemOverlay extends JavaScriptObject {

	public static final ProvidesKey<StrategyListItemOverlay> KEY_PROVIDER = new ProvidesKey<StrategyListItemOverlay>() {

		@Override
		public Object getKey(StrategyListItemOverlay item) {
			return item.getId();
		}
	};

	// Overlay types always have protected, zero-arg ctors
	protected StrategyListItemOverlay() { }
	
	public final native String getId() /*-{ return this.strategy; }-*/;
	public final native String getPublishedID() /*-{ return this.id; }-*/;
	public final native String getTitle() /*-{ return this.title; }-*/;

	/* for testing only */
	public final native static StrategyDefinitionOverlay create(String id, String name) /*-{ 
		return { "id": id, "strategy": name };  
	}-*/; 
}
