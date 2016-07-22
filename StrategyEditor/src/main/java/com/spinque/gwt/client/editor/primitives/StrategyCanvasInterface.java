package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPoint;

import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;

public interface StrategyCanvasInterface {

	public void addBlock(BuildingBlockInstanceOverlay json, BuildingBlockOverlay block);

	public void deleteBlock(SVGBuildingBlock svgBlock, boolean breakConnections);

	public void removeLine(SVGConnectionLine line);
	
	public void connect(final SVGSourcePoint sourcePoint, final SVGDestPoint destPoint);
	
	public void startTempLine(SVGConnectionPoint svgConnectionPoint, OMSVGPoint mouseLocation);
	public void endTempLine(SVGConnectionPoint endPoint);
	public void move(OMSVGGElement elem, float x, int y);
}
