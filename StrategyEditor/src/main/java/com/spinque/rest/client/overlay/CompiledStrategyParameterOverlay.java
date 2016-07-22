package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;

public class CompiledStrategyParameterOverlay extends JavaScriptObject {

	// TODO: implement
	
	protected CompiledStrategyParameterOverlay() { }

	/**
	 * @return REST-param name
	 */
	public final native String getID() /*-{ return this.id; }-*/;
	
	/**
	 * @return the type of the value that is expected.
	 */
	public final native String getDataType() /*-{ return this.datatype; }-*/;

	
	/**
	 * With getProperty() you can ask for things that are not per se 
	 * necessary for the REST-interface, but are useful when generating interfaces,
	 * such as:
	 * 	"title", "cluster", "defaultValue"
	 */
	public final native String getProperty(String property) /*-{ return this.properties[property]; }-*/;
	
	/**
	 * Tests whether the property is available.
	 * 
	 * @param property
	 * @return
	 */
	public final native boolean hasProperty(String property) /*-{ return this.properties[property] != null; }-*/;

	public final native <C extends JavaScriptObject> C getPropertyObject(String property) /*-{ return this.properties[property]; }-*/;

	
}
