package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGGElement;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class DraggableSVGElement implements MouseDownHandler, MouseUpHandler {

	// FIXME: switch SVG-canvas to AbsolutePanel. This makes it easier to switch to
	// HTML5. 
	
	private OMSVGGElement _obj;
	
	public void onAttachSVG() {
		_obj.addMouseDownHandler(this);
		_obj.addMouseUpHandler(this);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		
	}	
}
