package com.spinque.gwt.client.editor;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.dom.client.Style.BorderStyle;

public class Helpers {

	public static OMSVGLineElement createConnectionLine(OMSVGPoint sp, OMSVGPoint dp) {
		OMSVGDocument doc = OMSVGParser.currentDocument();
		OMSVGLineElement line = doc.createSVGLineElement(sp.getX(), sp.getY(), dp.getX(), dp.getY());
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY,SVGConstants.CSS_BLACK_VALUE);
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "3px");
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, "0.5");
		return line;
	}
	
	public static void updateStart(OMSVGLineElement line, OMSVGPoint position) {
		line.getX1().getBaseVal().setValue(position.getX());
		line.getY1().getBaseVal().setValue(position.getY());
	}

	public static void updateEnd(OMSVGLineElement line, OMSVGPoint position) {
		line.getX2().getBaseVal().setValue(position.getX());
		line.getY2().getBaseVal().setValue(position.getY());
	}

	public static OMSVGRectElement createTempGroupBox(
			OMSVGPoint position) {
		OMSVGDocument doc = OMSVGParser.currentDocument();
		OMSVGRectElement tempGroupBox = doc.createSVGRectElement(position.getX(), position.getY(), 0, 0, 5, 5);
		tempGroupBox.getStyle().setBorderStyle(BorderStyle.DASHED);
		tempGroupBox.getStyle().setBorderColor("#68B");
		tempGroupBox.getStyle().setOpacity(0.1);
		return tempGroupBox;
	}
	
	/**
	 * Puts the text at the center of the given line.
	 * @param text
	 * @param line
	 */
	public static void centerText(OMSVGTextElement text, OMSVGLineElement line) {
		float newX = line.getBBox().getCenterX();
		float newY = line.getBBox().getCenterY();
		text.getX().getBaseVal().getItem(0).setValue(newX);
		text.getY().getBaseVal().getItem(0).setValue(newY);
	}

}
