package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ControlOverlay;

public class BuildingBlockInstanceOverlay extends JavaScriptObject {
	
	/*
	 * {
    "buildingblock": "filter_DOC_str",
    "name": "filter_DOC_str_0",
    "y": "100.0",
    "controlinstances": [
        {
            "helpmessage": "<no help>",
            "control": "ATTRIBUTE",
            "title": "Title for ATTRIBUTE",
            "name": "filter_DOC_str_0_ATTRIBUTE",
            "cluster": "unassigned",
            "userconfigurable": "true"
        },
        {
            "helpmessage": "<no help>",
            "control": "OPERATION",
            "title": "Title for OPERATION",
            "default": "contains",
            "name": "filter_DOC_str_0_OPERATION",
            "cluster": "unassigned",
            "userconfigurable": "true"
        },
        {
            "helpmessage": "<no help>",
            "control": "VALUE",
            "title": "Title for VALUE",
            "name": "filter_DOC_str_0_VALUE",
            "cluster": "unassigned",
            "userconfigurable": "true"
        },
        {
            "helpmessage": "<no help>",
            "control": "CASESENSITIVE",
            "title": "Title for CASESENSITIVE",
            "default": "false",
            "name": "filter_DOC_str_0_CASESENSITIVE",
            "cluster": "unassigned",
            "userconfigurable": "true"
        }
    ],
    "x": "100.0"
}	 *
	 */
	
	public static class ControlInstanceOverlay extends JavaScriptObject { 
		protected ControlInstanceOverlay() { }
		public final native String getName() /*-{ return this.name; }-*/;
		public final native String getHelpMessage() /*-{ return this.helpmessage; }-*/;
		public final native String getTitle() /*-{ return this.title; }-*/;
		public final native String getValue() /*-{ return this.value; }-*/;
		public final native String getControl() /*-{ return this.control; }-*/;
		public final native String getCluster() /*-{ return this.cluster; }-*/;
		public final native boolean isUserConfigurable() /*-{ return this.userconfigurable; }-*/;
		public final native String getDataType() /*-{ return this.dataType; }-*/;
		public final native String getFormatOptions() /*-{ return this.formatOptions; }-*/;
		public static final native ControlInstanceOverlay create(ControlOverlay co) /*-{ 
			return {
				"control": co.name,
				"name": co.name,
				"title": "",
				"dataType": co.type,
				"formatOptions": co.format,
				"value": co["default"],
				"userconfigurable": false
			}; 
		}-*/;
		public final native void setValue(String newValue) /*-{ 
			this.value = newValue; 
		}-*/;
		
	}
	
	public static class OutputInstanceOverlay extends JavaScriptObject {
		protected OutputInstanceOverlay() { }
		public final native String getName() /*-{ return this.name; }-*/;
		public final native boolean getMaterialize() /*-{ return this.materialize; }-*/;
	}
	
	// Overlay types always have protected, zero-argument constructors
	protected BuildingBlockInstanceOverlay() { }
	  
	// Typically, methods on overlay types are JSNI
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getBuildingBlock() /*-{ return this.buildingblock; }-*/;
	public final native String getX() /*-{ return this.x; }-*/;
	public final native String getY() /*-{ return this.y; }-*/;
	public final native JsArray<ControlInstanceOverlay> getControls() /*-{ return this["controlinstances"]; }-*/;

	public final native void setControls(JsArray<ControlInstanceOverlay> controls) /*-{ this["controlinstances"] = controls; }-*/;

	public final native boolean setPosition(int x1, int y1) /*-{ 
		if (this.x == x1 && this.y == y1) {
	  		return false;
		} else {
			this.x = x1; this.y = y1;
			return true;
		} 
	}-*/;
	
	static int n = 0;
	
	public static final BuildingBlockInstanceOverlay create(BuildingBlockOverlay bbo, int x1, int y1) {
		String id = "block" + n++;
		BuildingBlockInstanceOverlay result = createInternal(bbo, id, x1, y1);
		result.setControls(buildControls(bbo));
		return result;
	}
	
	private static final JsArray<ControlInstanceOverlay> buildControls(BuildingBlockOverlay bbo) {
		JsArray<ControlInstanceOverlay> result = JsArray.createArray().cast();
		for (int i = 0; i < bbo.getControls().length(); i++) {
			ControlOverlay co = bbo.getControls().get(i);
			ControlInstanceOverlay cio  = ControlInstanceOverlay.create(co);
			result.push(cio);
		}
		return result;
	}

	private static final native BuildingBlockInstanceOverlay createInternal(BuildingBlockOverlay bbo, String id, int x1, int y1) /*-{
		return {
	    	"buildingblock": bbo.name,
	    	"name": id,
	    	"y": y1,
	    	"x": x1,
	    	"controlinstances": null
		}; 
	}-*/;
}
