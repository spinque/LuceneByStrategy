package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class FormatOptionsOverlay extends JavaScriptObject {
	
	// Overlay types always have protected, zero-arg ctors
	protected FormatOptionsOverlay() { }
	  
	public final native String getControl() /*-{ return this.control; }-*/;
	
	public final native String getType() /*-{ return this.controlType; }-*/;
	
	/* for image drop action */
	public final native String getDropField() /*-{ return this.dropField; }-*/;
	/* the strategy that yields a list of sample images, or null if no such strategy is provided */
	public final native String getSampleStrategyName() /*-{ return this.sampleStrategy; }-*/;

	/* for integer sliders */
	public final native int getMinValue() /*-{ return Number(this.min); }-*/;
	public final native int getMaxValue() /*-{ return Number(this.max); }-*/;

	/* misc properties */
	public final native boolean hasProp(String propName) /*-{ return this[propName] != null; }-*/;
	public final native String getProp(String propName) /*-{ return this[propName]; }-*/;
}
