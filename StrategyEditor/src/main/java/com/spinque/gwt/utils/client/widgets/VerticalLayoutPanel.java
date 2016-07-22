package com.spinque.gwt.utils.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Uses the available width and adjusts the child-widgets accordingly, but 
 * it does not grow child-widgets in terms of height. 
 */
public class VerticalLayoutPanel extends VerticalPanel implements ProvidesResize, RequiresResize {
	
	public VerticalLayoutPanel() {
		super();
		/* make sure the panel takes the full width */
		getElement().getStyle().setWidth(100, Unit.PCT);
	}
	
	public void onResize() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof RequiresResize)
				((RequiresResize)w).onResize();
		}
	}
}
