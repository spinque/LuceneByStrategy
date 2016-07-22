package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.NotificationOverlay;

public class ResultMarker implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private enum State { Pending, NoResult, Result };
	
	private final OMSVGGElement _resultMarker;
	private OMSVGElement _circleElement;
	
	private StrategyCanvas _strategyCanvas;
	private boolean dragging = false;
	private HandlerRegistration _moveHandler = null;
	private HandlerRegistration _upHandler;
	
	private OMSVGPoint _mouseStartPos = null; // location where the mouse started dragging the object
	private OMSVGMatrix _blockStartPos = null; // where the block was originally positioned

	private SVGSourcePoint _currentResult;
	
	public ResultMarker(OMSVGGElement resultMarker, StrategyCanvas strategyCanvas, OMSVGElement circleElement) {
		_resultMarker = resultMarker;
		_strategyCanvas = strategyCanvas;
		_circleElement = circleElement;
	}

	public void onAttachSVG() {
		_resultMarker.addMouseDownHandler(ResultMarker.this);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (dragging) {
			event.stopPropagation();
			event.preventDefault();
			return;
		}
		
		dragging = true;
		_mouseStartPos = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());
		if (_currentResult != null) {
			_blockStartPos = _resultMarker.getTransformToElement(_currentResult.sourcePoint);
		} else {
			_blockStartPos = _resultMarker.getTransformToElement(_strategyCanvas.canvas);
		}
		
//		if (_resultMarker.getParentNode() != _strategyCanvas.blockContainer) {
//			
//////			setMarkerPosition(_mouseStartPos);
//			_resultMarker.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "0.2");
//		}
		
		_moveHandler = _resultMarker.addMouseMoveHandler(this);
		_upHandler = _resultMarker.addMouseUpHandler(this);

		_resultMarker.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "0.5");
		DOMHelper.setCaptureElement(_resultMarker, null);

		// FIXME: start new drag...
		event.stopPropagation();
		event.preventDefault();
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		//		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
		//			event.stopPropagation();
		//		    event.preventDefault();
		//    		return;
		//    	}
		if (!dragging) {
			event.stopPropagation();
			event.preventDefault();
			return;
		}

		// FIXME: end drag...
		dragging = false;
		
		if (_moveHandler != null) {
			_moveHandler.removeHandler();
			_upHandler.removeHandler();
			_moveHandler = null;
			_upHandler = null;
		} else {
			event.stopPropagation();
			event.preventDefault();
			return;
		}
		
		SVGSourcePoint ssp = _strategyCanvas.getOverlappingSourcePoint(_resultMarker);
		if (ssp != null) {
			setResult(ssp);
		} else {
			unsetResult();
		}
		DOMHelper.releaseCaptureElement();
		
		event.stopPropagation();
		event.preventDefault();
	}

	private void setState(State newState) {
		switch (newState) {
		case Pending: 
			_circleElement.getStyle().setSVGProperty(SVGConstants.SVG_FILL_ATTRIBUTE, "#e0a060");
			_resultMarker.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "1.0");
			break;
		case NoResult:
			_circleElement.getStyle().setSVGProperty(SVGConstants.SVG_FILL_ATTRIBUTE, "#ff6000");	
			_resultMarker.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "0.5");
			break;
		case Result:
			_circleElement.getStyle().setSVGProperty(SVGConstants.SVG_FILL_ATTRIBUTE, "#60e060");
			_resultMarker.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "1.0");
			break;
		}
	}

	/**
	 * Asks the server to unmark the result, and then physically updates the 
	 * marker.
	 */
	public void unsetResult() {
		ServerSession.get().makeRequest(
				"unmarkresult", "", "", 
				new ResponseTask<NotificationOverlay>() {
					@Override
					public void run(NotificationOverlay json) {
						if (json.getStatus().equals(NotificationOverlay.OK)) {
							directUnsetResult();
						} else {
							// even if the server responds differently, we can't do 
							// anything else than just unset the result.
							directUnsetResult();
						}
						ServerSession.get().notifyChange();
					}

					@Override
					public void failed(Throwable exception, String debugDescription) {
//						Window.alert("Failed to unmark result");
					}
				}, "unmark result");
	}

	/**
	 * Without contacting the server, immediately unset the 
	 * result-marker from the block it is currently attached to and attach it to the main canvas.
	 * If the result-marker was already attached to the canvas nothing happens.
	 * 
	 * Note: only use when server is already aware of this change. 
	 */
	public void directUnsetResult() {
		attachToCanvas();
		setState(State.NoResult);
		_currentResult = null;
	}

	private void setResult(final SVGSourcePoint ssp) {
//		if (ServerSession.get().getWorkspaceDetails() == null) {
//			_currentResult = null;
//			setState(State.NoResult);
//			Window.alert("Init error!");
//			return;
//		}
		doMarkResult(ssp);
	}

	private void doMarkResult(final SVGSourcePoint ssp) {
		setState(State.Pending);
		ServerSession.get().setResult(ssp.getBBIName(),
				new ResponseTask<NotificationOverlay>() {
					@Override
					public void run(NotificationOverlay json) {
						markResult(ssp);
						ServerSession.get().notifyChange();
					}

					@Override
					public void failed(Throwable exception, String debugDescription) {
						attachToCanvas();
						_currentResult = null;
						setState(State.NoResult);
						ServerSession.get().notifyChange();
					}
				}, "mark result " + ssp.getBBIName() + " - " + ssp.getPointName());
	}

	public void markResult(SVGSourcePoint ssp) {
		_currentResult = ssp;
		attachToPoint(ssp.sourcePoint);
		setState(State.Result);
	}
	
	private void attachToPoint(OMSVGGElement sourcePoint) {
		_resultMarker.getTransform().getBaseVal().clear();
		sourcePoint.appendChild(_resultMarker);
	}

	private void attachToCanvas() {
		if (_currentResult != null) {
			OMSVGMatrix m = _strategyCanvas.canvas.getTransformToElement(_currentResult.rect).inverse();
			OMSVGTransform t = _strategyCanvas.canvas.createSVGTransformFromMatrix(m);
			_resultMarker.getTransform().getBaseVal().appendItem(t);
//			_resultMarker.getTransform().getBaseVal().consolidate();
		}
		_strategyCanvas.blockContainer.appendChild(_resultMarker);
	}

	@Override
	public synchronized void onMouseMove(MouseMoveEvent event) {
		if (dragging) {
			OMSVGPoint mouseLocation = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());
			setMarkerPosition(mouseLocation);
		}
		event.stopPropagation();
		event.preventDefault();
	}

	private void setMarkerPosition(OMSVGPoint mouseLocation) {
		OMSVGPoint d = mouseLocation.substract(_mouseStartPos);
        OMSVGMatrix newPos = _blockStartPos.translate(d.getX(), d.getY());

//        OMSVGTransform t = _strategyCanvas.canvas.createSVGTransform();
//        t.setTranslate(mouseLocation.getX(), mouseLocation.getY());
        
//        OMSVGTransform t = .createSVGTransformFromMatrix(newPos);
        OMSVGTransform t = _resultMarker.getTransform().getBaseVal().createSVGTransformFromMatrix(newPos);
//        if (_currentResult != null) {
//        	_currentResult.sourcePoint.getTransformToElement()
//        }
        _resultMarker.getTransform().getBaseVal().initialize(t);
	}

	/**
	 * Determines whether the ResultMarker is attached to
	 * one of the given points. 
	 * 
	 * @param sourcePoints
	 * @return
	 */
	public boolean isAttachedTo(SVGSourcePoint[] sourcePoints) {
		if (_currentResult == null) 
			return false;
		for (SVGSourcePoint ssp : sourcePoints) {
			if (ssp == _currentResult)
				return true;
		}
		return false;
	}
}
