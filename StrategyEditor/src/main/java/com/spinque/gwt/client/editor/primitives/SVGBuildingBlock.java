package com.spinque.gwt.client.editor.primitives;

import org.vectomatic.dom.svg.OMSVGForeignObjectElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.gwt.client.editor.panels.BuildingBlockEditPanel;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel;
import com.spinque.gwt.utils.client.dialog.SpinqueDialogBox;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.NotificationOverlay;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.ControlInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.OutputInstanceOverlay;

public class SVGBuildingBlock extends SVGImage implements MouseMoveHandler, MouseDownHandler, MouseUpHandler {
	
	interface MyUiBinder extends UiBinder<SVGImage, SVGBuildingBlock> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField public OMSVGGElement block;
	@UiField OMSVGRectElement blockRect;
	
	@UiField OMSVGGElement deleteRect;
	@UiField OMSVGGElement editRect;
	
	@UiField OMSVGTSpanElement titleText;
	@UiField OMSVGTSpanElement details1Text;
	@UiField OMSVGTSpanElement details2Text;
	@UiField OMSVGTSpanElement details3Text;
	@UiField OMSVGTSpanElement details4Text;
	
	@UiField OMSVGGElement sourcePoints;
	@UiField OMSVGGElement destPoints;
	
	@UiField OMSVGForeignObjectElement innerPane;

	private SVGSourcePoint[] _sourcePoints;

	private SVGDestPoint[] _destPoints;

	StrategyCanvas _strategyCanvas;

	private BuildingBlockOverlay _bbo;

	public BuildingBlockInstanceOverlay _instance;
//	private HandlerRegistration[] _sourcePointHandlers;
	private HandlerRegistration _editRectHR;
	private HandlerRegistration _deleteRectHR;

	interface Style extends CssResource {
		String ne();
		String blockTitle();
		String box();
	}
	
	public BuildingBlockInstanceOverlay getInstance() {
		return _instance;
	}
	
	InnerBuildingBlock _ibb;
	
	public SVGBuildingBlock(StrategyCanvas svg, BuildingBlockInstanceOverlay instance, 
				BuildingBlockOverlay bbo) {
		setSvgElement(uiBinder.createAndBindUi(this).getSvgElement());
		_strategyCanvas = svg;
		_bbo = bbo;
		_instance = instance;
		
		_ibb = new InnerBuildingBlock(this, bbo);
		
		HTMLPanel pane = HTMLPanel.wrap(innerPane.getElement());
		pane.add(_ibb);
		
		titleText.getElement().setInnerText(_instance.getBuildingBlock());
		
		_sourcePoints = new SVGSourcePoint[_bbo.getProvides().length()];
		_destPoints = new SVGDestPoint[_bbo.getNeeds().length()];
		
		for (int i = 0; i < _bbo.getProvides().length(); i++) {
			SVGSourcePoint sp = new SVGSourcePoint(_strategyCanvas, i, _instance.getName(), _bbo.getProvides().get(i));
			_sourcePoints[i] = sp;
			sourcePoints.appendChild(sp.sourcePoint);
		}
		for (int i = 0; i < _bbo.getNeeds().length(); i++) {
			SVGDestPoint dp = new SVGDestPoint(_strategyCanvas, i, _instance.getName(), _bbo.getNeeds().get(i));
			_destPoints[i] = dp;
			destPoints.appendChild(dp.destPoint);
		}
	}
	
	protected void onAttachSVG() {
		for (int i = 0; i < _sourcePoints.length; i++) {
			_sourcePoints[i].onAttachSVG();
		}
		for (int i = 0; i < _destPoints.length; i++) {
			_destPoints[i].onAttachSVG();
		}
		
		_deleteRectHR = deleteRect.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("Really delete this '" + _instance.getBuildingBlock() + "' block?")) {
					deleteBlock();
				}
			}
		});
		_editRectHR = editRect.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doEdit();
			}
		});
	}
	
	private void doEdit() {
		BuildingBlockEditPanel bbep = BuildingBlockEditPanel.generate(this);
		final SpinqueDialogBox popup = new SpinqueDialogBox(bbep, "Edit building block properties", DialogContentsPanel.APPLY | DialogContentsPanel.CANCEL);
		popup.doShow();
	}
	
	protected void onDetachSVG() {
		_editRectHR.removeHandler();
		_deleteRectHR.removeHandler();
		for (int i = 0; i < _sourcePoints.length; i++) {
			_sourcePoints[i].onDetachSVG();
		}
		for (int i = 0; i < _destPoints.length; i++) {
			_destPoints[i].onDetachSVG();
		}
	}

	int touchTimer = 0;

	public SVGConnectionPoint getSourcePoint(int i) {
		return _sourcePoints[i];
	}

	public SVGConnectionPoint getDestPoint(int i) {
		return _destPoints[i];
	}

	public void makeDraggable(StrategyCanvasInterface strategyWorkpanel) {
		block.addMouseDownHandler(this);
		block.addMouseUpHandler(this);
	}
	
	boolean dragging = false;
	OMSVGPoint p; // location where the mouse started dragging the object
	OMSVGMatrix p0; // where the block was originally positioned

	private HandlerRegistration _mouseMoveHandler = null;
	
	@Override
    public void onMouseUp(MouseUpEvent event) {

	    if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
//			doBlockPopup(event);
    		return;
    	}

		event.stopPropagation();
	    event.preventDefault();

	    dragging = false;
        if (_mouseMoveHandler != null) {
        	_mouseMoveHandler.removeHandler();
        	_mouseMoveHandler = null;
        }
        
        OMSVGPoint mouseLocation = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());
        OMSVGPoint d = _strategyCanvas.canvas.createSVGPoint(mouseLocation);
        OMSVGPoint location = d.substract(p).matrixTransform(p0);
        block.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "1.0");
    	if (_instance.setPosition(Math.round(location.getX()), Math.round(location.getY()))) {
        	ServerSession.get().makeRequest("relocate", "/" + _instance.getName() + "/" + _instance.getX() + "/" + _instance.getY(), 
        			"", new ResponseTask<NotificationOverlay>() {
        		@Override
        		public void run(NotificationOverlay json) {
        		}

        		@Override
        		public void failed(Throwable exception, String debugDescription) {
        		}
        	}, "move block");
        }
        DOMHelper.releaseCaptureElement();
    }
	
	@Override
    public void onMouseMove(MouseMoveEvent event) {
    	if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
//    		details2Text.getElement().setInnerText("Move: " + touchTimer++);
    		return;
    	}
    	OMSVGPoint mouseLocation = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());
    	_strategyCanvas.updateTempLine(mouseLocation);
        if (dragging) {
        	if (mouseLocation.getX() < 0) mouseLocation.setX(0);
        	if (mouseLocation.getY() < 0) mouseLocation.setY(0);
        	
        	mouseLocation.substract(p);
            OMSVGMatrix newPos = p0.translate(mouseLocation.getX(), mouseLocation.getY());
            OMSVGTransform t = block.getTransform().getBaseVal().createSVGTransformFromMatrix(newPos);
            block.getTransform().getBaseVal().initialize(t);
            
            
            
            for (SVGDestPoint p : _destPoints) {
            	_strategyCanvas.updateDest(p);
            }
            for (SVGSourcePoint p : _sourcePoints) {
            	_strategyCanvas.updateSrc(p);
            }
        }
        
        event.stopPropagation();
        event.preventDefault();
    }
    
    @Override
    public void onMouseDown(MouseDownEvent event) {

    	if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT)
    		return;
    	
    	event.stopPropagation();
        event.preventDefault();
    	
        dragging = true;
        p = _strategyCanvas.getLocalCoordinates(event.getClientX(), event.getClientY());
        p0 = block.getTransformToElement(_strategyCanvas.canvas); // getX().getBaseVal().getValue();
//        y0 = block.getY().getBaseVal().getValue();
        
        _mouseMoveHandler = block.addMouseMoveHandler(this);
        
        block.getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, "0.5");
//        _strategyCanvas.reAttach(this);
        DOMHelper.setCaptureElement(block, null);
    }

	public void moveTo(float x, float y) {
		OMSVGMatrix newPos = _strategyCanvas.canvas.createSVGMatrix().translate(x, y);
        OMSVGTransform t = block.getTransform().getBaseVal().createSVGTransformFromMatrix(newPos);
        block.getTransform().getBaseVal().initialize(t);
	}

	public SVGSourcePoint[] getSourcePoints() {
		return _sourcePoints;
	}

	public SVGDestPoint[] getDestPoints() {
		return _destPoints;
	}

	public BuildingBlockOverlay getBlock() {
		return _bbo;
	}

	public void update() {
		_ibb.update();
		
		// old update
		if (_instance.getControls().length() >= 1)
			displayControl(details1Text, _instance.getControls().get(0));
		if (_instance.getControls().length() >= 2)
			displayControl(details2Text, _instance.getControls().get(1));
		if (_instance.getControls().length() >= 3)
			displayControl(details3Text, _instance.getControls().get(2));
		if (_instance.getControls().length() >= 4)
			displayControl(details4Text, _instance.getControls().get(3));
	}
	
	private void displayControl(OMSVGTSpanElement textSpan,
			ControlInstanceOverlay c) {
		// value)
		if (c.getTitle() != null) {
			textSpan.getElement().setInnerText(
					(c.isUserConfigurable()  
						? "C " + c.getCluster() + ":" + c.getTitle() + " (" + c.getValue() + ")"
						: "F " + c.getControl() + " = " + c.getValue()) 
					);
			textSpan.getElement().getStyle().setColor("#e0a060");
		} else {
			textSpan.getElement().setInnerText("F " + c.getControl() + " = " + c.getValue());
			textSpan.getElement().getStyle().setColor("#a0a0a0");
		}
		textSpan.getStyle().setFontWeight(c.isUserConfigurable() ? FontWeight.BOLD : FontWeight.NORMAL);
	}

	public void deleteBlock() {
		ServerSession.get().doDelete(_instance.getName());
		_strategyCanvas.deleteBlock(SVGBuildingBlock.this, true);
		ServerSession.get().notifyChange();
	}
}
