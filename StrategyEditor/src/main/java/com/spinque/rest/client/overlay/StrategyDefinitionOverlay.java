package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.ProvidesKey;

public class StrategyDefinitionOverlay extends JavaScriptObject {

	public static final ProvidesKey<StrategyDefinitionOverlay> KEY_PROVIDER = new ProvidesKey<StrategyDefinitionOverlay>() {
		@Override
		public Object getKey(StrategyDefinitionOverlay item) {
			return item.getID();
		}
	};

	// Overlay types always have protected, zero-arg ctors
	protected StrategyDefinitionOverlay() { }

	// Typically, methods on overlay types are JSNI
	public final native String getID() /*-{ return this.properties.name; }-*/;
	public final native String getTitle() /*-{ return this.properties.title;  }-*/;
	public final native String getFolder() /*-{ return this.properties.folder;  }-*/; // |-separated path.
	public final native String getResultClass() /*-{ return this.resultClassName;  }-*/;
	public final native ResultDefinitionOverlay getResultDefinition() /*-{ return this.resultdefinition;  }-*/;
	
	public final native JsArray<StrategyParameterOverlay> getParameters() /*-{ return this.parameters;  }-*/;

	public final native StrategyCompositionOverlay getComposition() /*-{ return this.composition;  }-*/;

	public final String getFullPath() {
		return ((getFolder() != null) ? getFolder() + "/" : "") + getID();
	}
}
