package com.spinque.gwt.utils.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MultiColumnLayoutPanel extends LayoutPanel {
	
	public enum PlacementStrategy { ROUND_ROBIN }

	private final PlacementStrategy _placementStrategy;
	private final VerticalLayoutPanel[] _columns;
	private int _lastUsedColumn = -1;
	
	@UiConstructor
	public MultiColumnLayoutPanel(int numColumns, String placementStrategy) {
		this(numColumns, PlacementStrategy.valueOf(placementStrategy));
	}
	
	public MultiColumnLayoutPanel(int numColumns, PlacementStrategy placementStrategy) {
		super();
		_columns = new VerticalLayoutPanel[numColumns];
		_placementStrategy = placementStrategy;
		for (int i = 0; i < _columns.length; i++) {
			VerticalLayoutPanel panel = new VerticalLayoutPanel();
			ScrollPanel sp = new ScrollPanel(panel);
			super.add(sp);
			this.setWidgetLeftWidth(sp, ((100.0/_columns.length)*i), Unit.PCT, (100.0/_columns.length), Unit.PCT);
			_columns[i] = panel;
		}
	}
	
	@Override
	public void add(Widget widget) {
		switch (_placementStrategy) {
		case ROUND_ROBIN:
			_columns[++_lastUsedColumn % _columns.length].add(widget);
			break;
		default:
			throw new IllegalStateException("dont know how to handle " + _placementStrategy);
		}
	}
	
	@Override
	public boolean remove(Widget w) {
		for (VerticalLayoutPanel panel : _columns) {
			if (panel.remove(w))
				return true;
		}
		return false;
	}
	
	@Override
	public void clear() {
		_lastUsedColumn = -1;
		for (VerticalLayoutPanel panel : _columns) {
			panel.clear();
		}
	}
}
