package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class BuildingBlockCategoryOverlay extends JavaScriptObject implements Comparable<BuildingBlockCategoryOverlay> {

	public final native static BuildingBlockCategoryOverlay create(String id, String name, String description, int size) /*-{ 
		return { 
			"id": id, 
			"name": name,
			"description": description,
			"size": size
		};  
	}-*/; 
	
	protected BuildingBlockCategoryOverlay() { }
	
	public final native String getId() /*-{ return this.id; }-*/;
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getDescription() /*-{ return this.description; }-*/;
	public final native int getSize() /*-{ return this.size; }-*/;

	@Override
	public final int compareTo(BuildingBlockCategoryOverlay o) {
		return getName().compareTo(o.getName());
	}
}
