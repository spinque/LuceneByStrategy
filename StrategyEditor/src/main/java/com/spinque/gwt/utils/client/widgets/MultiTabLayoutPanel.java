package com.spinque.gwt.utils.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * TabLayoutPanel, to which tabs can be added that are displayed on the right-hand side
 * of the screen.
 * 
 */
public class MultiTabLayoutPanel extends TabLayoutPanel {

	public MultiTabLayoutPanel(double barHeight, Unit barUnit) {
		super(barHeight, barUnit);
	}
	
	public void addRight(IsWidget panel, String tabName) {
		Label label = new Label(tabName);
		add(panel, label);
		label.getParent().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		label.getParent().getElement().getStyle().setRight(4, Unit.PX);
		
		/* make sure the width of the tabs is set correctly 
		 * (user needs to make sure all tabs fit on the screen) */
		label.getParent().getParent().getElement().getStyle().setWidth(100, Unit.PCT);
	}
}
