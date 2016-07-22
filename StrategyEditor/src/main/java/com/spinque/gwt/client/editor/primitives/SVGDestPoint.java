package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ConnectionPointOverlay;

public class SVGDestPoint extends Composite implements SVGConnectionPoint, MouseDownHandler, MouseUpHandler {
	
	interface MyUiBinder extends UiBinder<Widget, SVGDestPoint> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField OMSVGGElement destPoint;
	@UiField OMSVGTextElement title;
	@UiField OMSVGRectElement rect;
	
	private final StrategyCanvas _strategyCanvas;
	private final int _index;
	private final ConnectionPointOverlay _cpo;
	private final String _bbiName;

	private HandlerRegistration _mouseUpHR;
	private HandlerRegistration _mouseDownHR;

	public SVGDestPoint(StrategyCanvas canvas, int i, String bbiName, ConnectionPointOverlay cpo) {
		initWidget(uiBinder.createAndBindUi(this));
		_index = i;
		_cpo = cpo;
		_bbiName = bbiName;
		_strategyCanvas = canvas;
		title.getElement().setInnerText(_cpo.getName());
		
		rect.getStyle().setSVGProperty("fill", _cpo.getTypeColor());
		destPoint.getElement().setTitle(_cpo.getDescription());
	}
	
	@Override
	public void onAttachSVG() {
		_mouseDownHR = destPoint.addMouseDownHandler(this);
		_mouseUpHR = destPoint.addMouseUpHandler(this);
		_strategyCanvas.move(destPoint, _index*60.0f, 0);
	}

	public void onDetachSVG() {
		if (_mouseDownHR != null) _mouseDownHR.removeHandler();
		if (_mouseUpHR != null) _mouseUpHR.removeHandler();
		_mouseDownHR = null;
		_mouseUpHR = null;
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
    		return;
    	}
		
		// start new _line...
		OMSVGPoint mouseLocation = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());

		if (!_strategyCanvas.lineBusy()) {
			_strategyCanvas.startTempLine(this, mouseLocation);
			//	        	DOMHelper.setCaptureElement(block, null);
			event.stopPropagation();
			event.preventDefault();
		}

		// FIXME: cannot set the background of a g element (will need the rectangle elemnt)
		//	        _point.getStyle().setSVGProperty(SVGConstants.CSS_BACKGROUND_VALUE, "#FD4");
	}

	@Override
	public boolean isSource() {
		return false;
	}

	public OMSVGRect getBBox() {
		return destPoint.getBBox();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
    		return;
    	}
		
		if (_strategyCanvas.lineBusy()) {
			_strategyCanvas.endTempLine(this);	
			event.stopPropagation();
			event.preventDefault();
		}
	}

	@Override
	public OMSVGGElement getSVGObject() {
		return destPoint;
	}
	
	public String getBBIName() {
		return _bbiName;
	}
	public String getPointName() {
		return _cpo.getName();
	}
}
