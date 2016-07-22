package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGGElement;

public interface SVGConnectionPoint {

	boolean isSource();

	OMSVGGElement getSVGObject();
	
	/* Should be called after a SVG-node is attached
	 * (as the SVGConnectionPoint cannot be attached (it is an SVGImage, not an element))  
	 * */ 
	void onAttachSVG();
}
