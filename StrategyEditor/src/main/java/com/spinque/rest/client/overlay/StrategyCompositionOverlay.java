package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class StrategyCompositionOverlay extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected StrategyCompositionOverlay() { }

	public final native JsArray<BuildingBlockInstanceOverlay> getBlocks()  /*-{ return this.blocks;  }-*/;
	public final native JsArray<ConnectionOverlay> getConnections()  /*-{ return this.connections;  }-*/;

	public static class ConnectionOverlay extends JavaScriptObject { 
		
		protected ConnectionOverlay() { }
		
		public final native String getSourceName() /*-{ return this.source; }-*/;
		public final native String getDestName() /*-{ return this.destination; }-*/;

		public final String getSourceBlock() {
			int splitSource = getSourceName().lastIndexOf('_');
			return getSourceName().substring(0,splitSource);
		}
		public final String getSourcePoint() {
			int splitSource = getSourceName().lastIndexOf('_');
			return getSourceName().substring(splitSource+1);
		}
		public final String getDestBlock() {
			int splitDest = getDestName().lastIndexOf('_');
			return getDestName().substring(0,splitDest);
		}
		public final String getDestPoint() {
			int splitDest = getDestName().lastIndexOf('_');
			return getDestName().substring(splitDest+1);
		}
	}
}
