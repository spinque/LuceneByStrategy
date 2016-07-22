package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Represents an item like:
 *  
{
	"total": 37,
	"stats": [
    	<StatisticsItemOverlay>
	]
}
 */
public class StatisticsOverlay extends JavaScriptObject {
	
	// Overlay types always have protected, zero-arg ctors
	protected StatisticsOverlay() { }

	public final native int getTotal() /*-{ return this.total; }-*/;
	public final native JsArray<StatisticsItemOverlay> getStats() /*-{ return this.stats; }-*/;

	/**
	 * Represent an item like:  
        {
            "cutoff": "0.01",
            "numResults": 28
        }
	 */
	public static class StatisticsItemOverlay extends JavaScriptObject {
		
		// Overlay types always have protected, zero-arg ctors
		protected StatisticsItemOverlay() { }

		public final native String getCutOff() /*-{ return this.cutoff; }-*/;
		public final native int getNumResults() /*-{ return this.numResults; }-*/;
	}
}
