package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.spinque.gwt.client.editor.ServerSession;

public class SVGConnectionLine implements ClickHandler, MouseOverHandler, MouseOutHandler {
	private final OMSVGLineElement _line;
//	private final OMSVGTextElement _text;
	
	final String _destPointName;
	final String _bbiName;
	
	private OMSVGGElement _lineContainer;
	private HandlerRegistration _mouseOutHR;
	private HandlerRegistration _mouseOverHR;
	private HandlerRegistration _clickHR;
	private SVGSourcePoint _sourcePoint;
	private SVGDestPoint _destPoint;
	private final StrategyCanvas _canvas;
	
	SVGSourcePoint getSourcePoint() {
		return _sourcePoint;
	}
	SVGDestPoint getDestPoint() {
		return _destPoint;
	}
	
	public SVGConnectionLine(OMSVGGElement lineContainer, OMSVGPoint sp, OMSVGPoint dp, String bbiName, 
			String destPointName, SVGSourcePoint sourcePoint, SVGDestPoint destPoint, StrategyCanvas canvas) {
		_line = createLine(sp, dp);
//		_text = createText(sp, dp, sourcePoint.getPointType());

		_bbiName = bbiName;
		_destPointName = destPointName;
		_lineContainer = lineContainer;
		
		_sourcePoint = sourcePoint;
		_destPoint = destPoint;
		
		// add new final _line
		_lineContainer.appendChild(_line);
//		_lineContainer.appendChild(_text);
		_clickHR = _line.addClickHandler(this);
		_mouseOverHR = _line.addMouseOverHandler(this);
		_mouseOutHR = _line.addMouseOutHandler(this);
		
		_canvas = canvas;
	}
	
//	private OMSVGTextElement createText(OMSVGPoint sp, OMSVGPoint dp,
//			String tpe) {
//		return new OMSVGTextElement((sp.getX() + dp.getX()) / 2,(sp.getY() + dp.getY()) / 2, OMSVGTextElement.LENGTHADJUST_SPACING, "Type: " + tpe);
//	}

	private OMSVGLineElement createLine(OMSVGPoint sp, OMSVGPoint dp) {
		OMSVGDocument doc = OMSVGParser.currentDocument();
		OMSVGLineElement line = doc.createSVGLineElement(sp.getX(), sp.getY(), dp.getX(), dp.getY());
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY,SVGConstants.CSS_BLACK_VALUE);
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "5px");
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, "0.5");
		return line;
	}

	public void onDetachSVG() {
		_clickHR.removeHandler();
		_mouseOverHR.removeHandler();
		_mouseOutHR.removeHandler();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		ServerSession.get().disconnect(this, _canvas);
	}

	/**
	 * Does not notify server. Just deletes the graphical 
	 * representation of the line.
	 *  
	 * @param line
	 */
	public void removeLine() {
		onDetachSVG();
		_lineContainer.removeChild(_line);
//		_lineContainer.removeChild(_text);
	}
	
	public OMSVGLineElement getLine() {
		return _line;
	}

//	public OMSVGTextElement getText() {
//		return _text;
//	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		_line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "#FF0000");
		_line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, "1.0");
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		setOriginalState();
	}
	
	public void setPendingDelete() {
		_line.getStyle().setSVGProperty(SVGConstants.SVG_STROKE_PAINT_VALUE, "#FFD040");
	}
	
	public void setOriginalState() {
		_line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		_line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, "0.5");
	}
	
	public String getBBIName() {
		return _bbiName;
	}
	
	public String getDestPointName() {
		return _destPointName;
	}
}
