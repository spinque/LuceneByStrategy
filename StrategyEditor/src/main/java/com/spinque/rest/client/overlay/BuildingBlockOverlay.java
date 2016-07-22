package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class BuildingBlockOverlay extends JavaScriptObject {
	
	public static final String RESULT_BLOCK_CLASS = "com.spinque.strategy.blocks.ResultBlock";
	
	
	public static class ConnectionPointOverlay extends JavaScriptObject { 
		protected ConnectionPointOverlay() { }
		public final native String getName() /*-{ return this.name; }-*/;
		public final native String getType() /*-{ return this.type; }-*/;
		public final native String getDescription() /*-{ return this.description; }-*/;
		
		public final native static ConnectionPointOverlay create(String name, String type) /*-{ 
		return { 
			"name": name, 
			"type": type
		};  
	}-*/; 
		
//		 4a67c4
//		  4ac467
//		  674ac4
//		  67c44a
//		  c44a67
//		  c4674a
		public final String getTypeColor() {
			if (getType().equals("DOC_ID")) {
				return "#4a67c4";
			} else if (getType().equals("SECTION_ID")) {
				return "#4ac467";
			} else if (getType().equals("NE")) {
				return "#674ac4";
			} else if (getType().equals("TUPLE")) {
				return "#67c44a";
			} else if (getType().equals("ANY")) {
				return "#c4674a";	
			}
			return "#aaaaaa";
		}
	}
	
	public static class ControlOverlay extends JavaScriptObject { 
		protected ControlOverlay() { }
		
		public final native static ControlOverlay create(String name, String type, String format, String defaultValue) /*-{ 
		return { 
			"name": name, 
			"type": type,
			"format": format,
			"default": defaultValue
		};  
	}-*/; 

		public final native String getControl() /*-{ return this.name; }-*/;
		public final native String getType() /*-{ return this.type; }-*/;
		public final native String getFormat() /*-{ return this.format; }-*/;
		public final native String getDefaultValue() /*-{ return this["default"]; }-*/;
//		public final native String getValues() /*-{ return this.default; }-*/;
		public final native String getList() /*-{ return this.list; }-*/;
		
		@Deprecated // obsolete in 2.0
		public final native String getName() /*-{ return this.name; }-*/;
	}
	
	protected BuildingBlockOverlay() { } /* Overlay types always have protected, zero-arg ctors */
	

	public final native static BuildingBlockOverlay create(String className, String name, String description,
			JsArray<ConnectionPointOverlay> inputs, 
			JsArray<ConnectionPointOverlay> outputs,
			JsArray<ControlOverlay> controls) /*-{ 
	return { 
		"className": className, 
		"name": name,
		"details": {
			"description": description,
			"code": {
				"xml": "<block type=\"" + className + "\"/>"
			}
		},
		"needs": inputs,
		"provides": outputs,
		"controls": controls
	};  
}-*/; 
	  
	public final native String getClassName() /*-{ return this.className; }-*/;

	/* Meta data*/
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getIcon() /*-{ return this.icon; }-*/;
	public final native String getDescription() /*-{ return this.details.description; }-*/;
	
	/*  2.0 needs: */
//	public final native String getName() /*-{ return this.meta.name; }-*/;
//	public final native String getIcon() /*-{ return this.meta.icon; }-*/;
//	public final native String getDescription() /*-{ return this.meta.description; }-*/;

	/* IO */
	public final native JsArray<ConnectionPointOverlay> getNeeds() /*-{ return this.needs; }-*/;
	public final native JsArray<ConnectionPointOverlay> getProvides() /*-{ return this.provides; }-*/;
	public final native JsArray<ControlOverlay> getControls() /*-{ return this.controls; }-*/;
//	public final native JsArray<ConnectionPointOverlay> getNeeds() /*-{ return this.io.inputs; }-*/;
//	public final native JsArray<ConnectionPointOverlay> getProvides() /*-{ return this.io.outputs; }-*/;
//	public final native JsArray<ControlOverlay> getControls() /*-{ return this.io.parameters; }-*/;
	
	/* Specific to script-blocks */
	public final native String getCode(String language) /*-{
	 	return this.details.code[language]; 
	}-*/;
//	public final native String getCode(String language) /*-{
//	 	return this.code[0].script; 
//	}-*/;
}
