package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

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

public class SVGSourcePoint extends Composite implements SVGConnectionPoint, MouseDownHandler, MouseUpHandler {

	interface MyUiBinder extends UiBinder<Widget, SVGSourcePoint> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public enum ResultState { IS_RESULT, PENDING, IS_NO_RESULT }; 
	
	@UiField OMSVGGElement sourcePoint;
	@UiField OMSVGTextElement title;
	@UiField OMSVGRectElement rect;
	private final StrategyCanvas _strategyCanvas;

	int _index;
	private final ConnectionPointOverlay _cpo;
	private final String _bbiName;
	private boolean _materialize = false; // is false by default, will be injected via the SVGBuildingBlock-constructor.
	
	private HandlerRegistration _mouseDownHR;
	private HandlerRegistration _mouseUpHR;

	public SVGSourcePoint(StrategyCanvas canvas, int i, String bbiName, ConnectionPointOverlay cpo) {
		initWidget(uiBinder.createAndBindUi(this));
		_index = i;
		_cpo = cpo;
		_bbiName = bbiName;
		_strategyCanvas = canvas;
		title.getElement().setInnerText(_cpo.getName());
		rect.getStyle().setSVGProperty("fill", cpo.getTypeColor());
		sourcePoint.getElement().setTitle(_cpo.getDescription());
	}
	
	@Override
	public void onAttachSVG() {
		_mouseDownHR = sourcePoint.addMouseDownHandler(this);
		_mouseUpHR = sourcePoint.addMouseUpHandler(this);
		_strategyCanvas.move(sourcePoint, -_index*60.0f, 0);
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
//			doBlockPopup(event);
//			event.stopPropagation();
//		    event.preventDefault();
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

		// FIXME: cannot set the background of a g element (will need the rectangle element)
		//	        _point.getStyle().setSVGProperty(SVGConstants.CSS_BACKGROUND_VALUE, "#FD4");
	}

	@Override
	public boolean isSource() {
		return true;
	}

	public OMSVGRect getBBox() {
		return sourcePoint.getBBox();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
//			event.stopPropagation();
//		    event.preventDefault();
    		return;
    	}
		if (_strategyCanvas.lineBusy()) {
			_strategyCanvas.endTempLine(this);	
			event.stopPropagation();
			event.preventDefault();
		}
	}
	
//	private void doBlockPopup(MouseDownEvent event) {
//		PopupPanel pp = new PopupPanel(true, true);
//		pp.setAnimationEnabled(true);
//		
//		pp.setPopupPosition(event.getClientX(), event.getClientY());
//		
//		SourcePointMenu bbm = new SourcePointMenu(pp, this);
////		
////		MenuBar popupMenuBar = new MenuBar(true);
////		MenuItem editItem = new MenuItem("Edit Properties", true, new Command() {
////			@Override
////			public void execute() {
////				Window.alert("FIXME: implement: edit properties");
////			}
////		});
//////		MenuItem imageItem = new MenuItem("Don't know", true, new Command() {
//////			@Override
//////			public void execute() {
//////				Window.alert("FIXME: implement: delete block");
//////				
//////			}
//////		});
////		MenuItem deleteItem = new MenuItem("Delete block", true, new Command() {
////			@Override
////			public void execute() {
////				Window.alert("FIXME: implement: delete block");
////			}
////		});
////		 
//////		  popupPanel.setStyleName("popup");
//////		  alertItem.addStyleName("popup-item");
//////		  imageItem.addStyleName("popup-item");
//////		  sponserItem.addStyleName("popup-item");
////		 
////		  popupMenuBar.addItem(editItem);
//////		  popupMenuBar.addItem(imageItem);
////		  popupMenuBar.addItem(deleteItem);
////		 
////		  popupMenuBar.setVisible(true);
//		  pp.add(bbm);
//		  pp.show();
//	}

	@Override
	public OMSVGGElement getSVGObject() {
		return sourcePoint;
	}

	public String getBBIName() {
		return _bbiName;
	}

	public String getPointName() {
		return _cpo.getName();
	}
	
	public String getPointType() {
		return _cpo.getType();
	}

	public boolean getMaterialize() {
		return _materialize;
	}

	public void setMaterialize(boolean materialize) {
		_materialize = materialize;
		rect.getStyle().setSVGProperty(SVGConstants.SVG_STROKE_ATTRIBUTE, _materialize ? "#F00" : "#000"); 
		rect.getStyle().setSVGProperty(SVGConstants.SVG_STROKE_WIDTH_ATTRIBUTE, (_materialize ? 2 : 1) + "px");
	}

//	public void setResult(ResultState rs) {
//		switch (rs) {
//		case IS_NO_RESULT:
//			resultMark.getStyle().setSVGProperty(SVGConstants.SVG_OPACITY_ATTRIBUTE, "0.0");
//			break;
//		case IS_RESULT:
//			resultMark.getStyle().setSVGProperty(SVGConstants.SVG_OPACITY_ATTRIBUTE, "1.0");
//			resultMark.getStyle().setSVGProperty(SVGConstants.SVG_FILL_ATTRIBUTE, "#FFAA40");
//			break;
//		case PENDING:
//			resultMark.getStyle().setSVGProperty(SVGConstants.SVG_OPACITY_ATTRIBUTE, "0.5");
//			resultMark.getStyle().setSVGProperty(SVGConstants.SVG_FILL_ATTRIBUTE, "#FFAA40");
//			break;
//		}
//	}
}
