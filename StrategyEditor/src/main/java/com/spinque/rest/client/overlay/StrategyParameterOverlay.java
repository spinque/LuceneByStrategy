package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class StrategyParameterOverlay extends JavaScriptObject {
	// Overlay types always have protected, zero-arg ctors
	protected StrategyParameterOverlay() { }

	public final native String getID() /*-{ return this.id; }-*/;
	public final native String getName()  /*-{ return this.name;  }-*/;
	public final native String getDataType()  /*-{ return this.datatype;  }-*/;

	public final native boolean isDefaultValueObject() /*-{ 
		return (this.defaultValue instanceof Object);  
	}-*/;
	
	public final native <T extends JavaScriptObject> T getDefaultValue()   /*-{ return this.defaultValue;  }-*/;

	public final native String getDefaultStringValue() /*-{ return this.defaultValue;  }-*/;
	
	public final native FormatOptionsOverlay getFormatOptions()   /*-{ return this.format;  }-*/;
	
	public final native String getScale() /*-{ return this.scale;  }-*/; // valid values: null, 'lin(ear)', 'log(arithm)', 'sin(e)'
	
	// FIXME: test/implement
	public final native boolean isUserConfigurable() /*-{ return this.userConfigurable;  }-*/;
//	public final native String getParamName() /*-{ return this.paramName;  }-*/;
	public final native String getTitle() /*-{ return this.title;  }-*/;
	public final native String getClusterName() /*-{ return this.cluster;  }-*/;
	
}
