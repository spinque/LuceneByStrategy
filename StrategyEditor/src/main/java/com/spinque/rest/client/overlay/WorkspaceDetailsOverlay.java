package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class WorkspaceDetailsOverlay extends JavaScriptObject {

	
	/**
	 * 
	 * {
    "name": "alexandria",
    "collections": [

    ],
    "classes": {
        "DOC_ID": [
            "patent-document": {
                "name": "patent-document"
            }
        ],
        "SECTION_ID": [

        ],
        "TERM": [
            "": {
                "name": ""
            }
        ],
        "NE": [
            "company": {
                "name": "company"
            },
            "country": {
                "name": "country"
            },

	 */
	
	
//	private final String _collectionName;
//	private int _size;

	public static class ConceptClassOverlay extends JavaScriptObject {
		protected ConceptClassOverlay() { }
		public final native String getName() /*-{ return this.name; }-*/;
	}
	
	protected WorkspaceDetailsOverlay() { }
	
	public final native String getName() /*-{ return this.name; }-*/;
	public final native JsArray<ConceptClassOverlay> getClasses(String typeName) /*-{ return this.classes[typeName]; }-*/;
	public final native JsArray<JavaScriptObject> getCollections() /*-{ return this.collections; }-*/;

}
