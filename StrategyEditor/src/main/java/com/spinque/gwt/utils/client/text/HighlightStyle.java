package com.spinque.gwt.utils.client.text;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface HighlightStyle extends ClientBundle {

	public static HighlightStyle INSTANCE = GWT.create(HighlightStyle.class);
	
	@Source("highlight.css")
	HighlightCss style();
	
	//.highlight1 { background-color: #DDDD66; }
	public interface HighlightCss extends CssResource {
		String highlight1();
		String highlight2();
		String highlight3();
		String highlight4();
		String highlight5();
	}
	
	
}
