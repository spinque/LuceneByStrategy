package com.spinque.gwt.utils.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Makes a widget which centers a LayoutPanel on the screen.
 * The width of the child panel is fixed, the height is 100%.
 * When the parent-widget changes size (or the browser is resized), the
 * child-widget will automatically be positioned in the middle (leaving equal space on both sides).
 */
public class CenterLayoutPanel extends LayoutPanel implements ProvidesResize, RequiresResize {

	private final Widget _child;
	private final int _width;
	
	public CenterLayoutPanel(Widget child, int width) {
		_width = width;
		_child = child;
		add(_child);
	}
	
	@Override
	protected void onLoad() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		});
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		reposition();
	}

	public void onResize() {
		reposition();
		if (_child instanceof RequiresResize)
			((RequiresResize)_child).onResize();
	}
	
	private void reposition() {
		int totalWidth = getElement().getClientWidth();
		int totalBorderWidth = totalWidth - _width;
		setWidgetLeftWidth(_child, (totalBorderWidth > 0) ? totalBorderWidth / 2 : 0, Unit.PX, _width, Unit.PX);
	}
}
