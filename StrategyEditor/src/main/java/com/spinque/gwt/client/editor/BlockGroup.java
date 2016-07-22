package com.spinque.gwt.client.editor;

import org.vectomatic.dom.svg.OMSVGRect;


public class BlockGroup {
	
	// TODO: implement "drop block on line":
	// adds the block in between the source of the line and the dest of the line.

	private double _x;
	private double _y;
	private double _width; // > 0
	private double _height; // > 0
	private String _title; // optional
	private boolean _collapsedState;
	
	/* Does the group have to include a list of blocks...????
	
	public static BlockGroup create(OMSVGRect bBox) {
		// FIXME:
		// - find all buildingblocks inside
		// - find all the lines between blocks in the group
		// - draw the group border (and icons to minimize it)
		// - 
		return null;
	}
	

	/*
	 * - find all non-connected inputs
	 * - find all connected outputs
	 * - minimize (draw the inputs and outputs, and a maximize button)
	 * - if there are blocks south of the group on the canvas: move them up
	 * - otherwise if there are blocks to the right (either east or south-east): move them to the left
	 *    
	 */
	public void minimizeGroup() {
		
	}
	
	public void maximizeGroup() {
		
	}

	public static BlockGroup create(OMSVGRect bBox) {
		// TODO Auto-generated method stub
		return null;
	}
}
