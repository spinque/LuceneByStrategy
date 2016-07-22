package com.spinque.gwt.client.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface SpinqueStyle extends ClientBundle {
	
	public final static SpinqueStyle INSTANCE = GWT.create(SpinqueStyle.class);
	
	interface SpinqueCss extends CssResource {
		String spinquePadForTab();
		String spinqueTheActualTab();
		
		String spinqueBox();
		String spinqueColumnHeading();
		String spinqueButton();
		
		/* from parameter items */
		String spinqueParameter();
		String spinqueAppliedFacet();
		String spinqueParamLabel();
		String spinqueTextareaInput();
	}
	
	@Source("default.css")
	public SpinqueCss style();
}
