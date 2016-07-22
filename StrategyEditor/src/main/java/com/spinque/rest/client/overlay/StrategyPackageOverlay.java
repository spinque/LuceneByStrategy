package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class StrategyPackageOverlay extends JavaScriptObject {
	
	protected StrategyPackageOverlay() { }

	public final native StrategyDefinitionOverlay getStrategy() /*-{ return this.strategy; }-*/;
	public final native BuildingBlockOverlay getBlock(String blockName) /*-{ return this.blocks[blockName]; }-*/;
}
