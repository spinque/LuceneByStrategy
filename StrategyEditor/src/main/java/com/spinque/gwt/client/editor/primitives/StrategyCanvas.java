package com.spinque.gwt.client.editor.primitives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.EditorApplication;
import com.spinque.gwt.client.editor.BlockGroup;
import com.spinque.gwt.client.editor.Helpers;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.gwt.client.editor.library.LibraryBuildingBlockCell;
import com.spinque.gwt.utils.client.Utils;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.StrategyCompositionOverlay.ConnectionOverlay;

public class StrategyCanvas extends Composite implements MouseMoveHandler, MouseUpHandler, MouseDownHandler, ClickHandler,  StrategyCanvasInterface {
	
	private static final float MAX_DIST_SOURCE_POINT_2 = 20f * 20f; // squared

	interface MyUiBinder extends UiBinder<Widget, StrategyCanvas> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField AbsolutePanel workpane;
	@UiField Label sessionIDField;

	@UiField SVGImage svgImg;
	@UiField OMSVGSVGElement canvas;
	@UiField OMSVGGElement lineContainer;
	@UiField OMSVGGElement blockContainer;
	
	@UiField OMSVGGElement resultMarker;
	@UiField OMSVGElement resultCircleElement;
	
	private PickupDragController _dragController;
	private StrategyPanelDropController _dropController;

	private OMSVGLineElement _tempLine = null;
	private SVGConnectionPoint _tempPoint = null;
	private boolean _groupCreationInProgress;
	private OMSVGPoint _groupCreationStart;
	private OMSVGRectElement _tempGroupBox;
	private final ResultMarker _resultMarker;
	private final List<SVGBuildingBlock> _blocks = new ArrayList<SVGBuildingBlock>();
	
	// lines are always from source to dest
	private final Map<SVGConnectionPoint, Set<SVGConnectionLine>> _connPoint 
						= new HashMap<SVGConnectionPoint, Set<SVGConnectionLine>>();
	
	class StrategyPanelDropController extends AbsolutePositionDropController {

		public StrategyPanelDropController() {
			super(workpane);
		}
		
		// should convert from LibraryBuildingBlockCell to SVGBuildingBlock here
		@Override
		public void onDrop(DragContext context) {
			
			Utils.log("ondrop " + context.draggable);
			
			EditorApplication.dragController.makeNotDraggable(context.draggable);
			context.draggable.removeFromParent();
			
			LibraryBuildingBlockCell lbbc = (LibraryBuildingBlockCell) (context.draggable);
			
			Utils.log("ondrop " + lbbc.getBuildingBlockOverlay().getName());
			
			// context.desiredDraggableX, context.desiredDraggableY
			OMSVGPoint p = getLocalCoordinates(context.mouseX - Window.getScrollLeft(), 
					context.mouseY - Window.getScrollTop());
			addBlock(lbbc.getBuildingBlockOverlay(), p, StrategyCanvas.this);
		}
		
		private void addBlock(final BuildingBlockOverlay block, OMSVGPoint point, final StrategyCanvas canvas) {
			ServerSession.get().addBlock(block, Math.round(point.getX()), Math.round(point.getY()),
					new ResponseTask<BuildingBlockInstanceOverlay>() {
				@Override
				public void run(BuildingBlockInstanceOverlay json) {
					canvas.addBlock(json, block);
					ServerSession.get().notifyChange();
				}

				@Override
				public void failed(Throwable exception, String debugDescription) {
					Window.alert("Failed to add block: " + exception.getMessage());
				}
			}, "add block " + block.getName());
		}

		@Override
		public void onPreviewDrop(DragContext context) throws VetoDragException {
			// FIXME: allow connections to be drawn
			Utils.log("preview drop " + context);
			
			if (!"yes".equals(context.draggable.getElement().getAttribute("block"))) {
				Utils.log("no block");
				throw new VetoDragException();
			}
					
			
			// only allow BuildingBlockCell to be dropped
//			if (!(context.draggable instanceof LibraryBuildingBlockCell)) {
//				throw new VetoDragException();
//			}
			super.onPreviewDrop(context);
		}
	}

	public StrategyCanvas() {
		initWidget(uiBinder.createAndBindUi(this));
		_dragController = EditorApplication.prepareDragController(workpane);
//		_dragController = EditorApplication.dragController;
		_resultMarker = new ResultMarker(resultMarker, this, resultCircleElement);
	}
	
	int imageWidth = 0;
	int imageHeight = 0;


//	@Override
//	public void onResize() {
////		super.onResize();
//		showSessionID();
//		
//		/* the work pane is as wide as the screen, or wider if blocks are outside the screen */
//		workpane.setWidth(Math.max(getParent().getOffsetWidth(), imageWidth) + "px");
//		workpane.setHeight(Math.max(getParent().getOffsetHeight(), imageHeight) + "px");
//		svgImg.setWidth(getOffsetWidth() + "px");
//		svgImg.setHeight(getOffsetHeight() + "px");
//	}
	
	public void initSession(final Callback<Void, Throwable> callback) {
		// FIXME: clear??
		showSessionID();
		callback.onSuccess(null);
//		ServerSession.get().makeRequest("refresh", "", "", new ResponseTask<StrategyPackageOverlay>() {
//
//			@Override
//			public void failed(Throwable exception, String debugDescription) {
//				// Do nothing?
////				Window.alert("oops: could not load the current strategy");
//				if (callback != null)
//					callback.onFailure(exception);
//			}
//
//			@Override
//			public void run(StrategyPackageOverlay pack) {
//				StrategyDefinitionOverlay strategy = pack.getStrategy();
//				StrategyCompositionOverlay sco = strategy.getComposition();
//				for (int i = 0; i < sco.getBlocks().length(); i++) {
//					BuildingBlockInstanceOverlay bbio = sco.getBlocks().get(i);
//					BuildingBlockOverlay bbConcept = pack.getBlock(bbio.getBuildingBlock());
//					
//					// make sure that the used blocks is complete
//					ServerSession.get().addToKnownBlocks(bbConcept);
//					addBlock(bbio, bbConcept);
//				}
//				for (int i = 0; i < sco.getConnections().length(); i++) {
//					ConnectionOverlay co = sco.getConnections().get(i);
//					makeLine(co);
//				}
//				// set result
//				if (strategy.getResultDefinition() != null) {
//					String resultStr = strategy.getResultDefinition().getSourcePoint();
//					int splitPoint = resultStr.lastIndexOf('_');
//					SVGSourcePoint ssp = findSourcePoint(resultStr.substring(0, splitPoint), resultStr.substring(splitPoint + 1));
//					_resultMarker.markResult(ssp);
//				}
//				ServerSession.get().notifyChange();
//				
//				if (callback != null)
//					callback.onSuccess(null);
//			}
//		}, "refresh strategy");
//		workpane.setWidgetPosition(svgImg, 0, 0);
	}
	
	private void showSessionID() {
		if (ServerSession.isInitialized()) {
			sessionIDField.setText("SessionID: " + ServerSession.get().getSessionID() + " (X=" + imageWidth + ",Y=" + imageHeight +")");
		} else {
			sessionIDField.setText("(X=" + imageWidth + ",Y=" + imageHeight +")");
		}
	}

	@Override
	protected void onLoad() {
		_dropController = new StrategyPanelDropController();
	    _dragController.registerDropController(_dropController);
	    
	    canvas.addMouseUpHandler(this);
		canvas.addMouseMoveHandler(this);
		canvas.addMouseDownHandler(this);
		_resultMarker.onAttachSVG();
//		onResize();
	}
	
	@Override
	protected void onUnload() {
		_dragController.unregisterDropController(_dropController);
	}

	@Override
	public void addBlock(BuildingBlockInstanceOverlay bbio, BuildingBlockOverlay bbo) {
		SVGBuildingBlock svgBuildingBlock = new SVGBuildingBlock(StrategyCanvas.this, 
				bbio, bbo);
		
		// will onAttach be called?
		blockContainer.appendChild(svgBuildingBlock.block); // getSvgElement()
		svgBuildingBlock.onAttachSVG();
		svgBuildingBlock.moveTo(Float.parseFloat(bbio.getX()), Float.parseFloat(bbio.getY()));
		svgBuildingBlock.makeDraggable(StrategyCanvas.this);		
		_blocks.add(svgBuildingBlock);
		svgBuildingBlock.update();
	}
	
	@Override
	public void deleteBlock(SVGBuildingBlock svgBlock, boolean breakConnections) {
		// remove the connections with the block
		Utils.log("deleting block");
		if (breakConnections) {
			for (SVGDestPoint sdp : svgBlock.getDestPoints()) {
				deleteLines(sdp);
			}
			for (SVGSourcePoint ssp : svgBlock.getSourcePoints()) {
				deleteLines(ssp);
			}
		}
		
		// if the block has the 'result-marker' set to it, detach it.
		if (_resultMarker.isAttachedTo(svgBlock.getSourcePoints())) {
			_resultMarker.directUnsetResult();
		}
		Utils.log("deleting block halfway");
		
		// clean-up listeners that are registered with 'block'.
		svgBlock.onDetachSVG();

		// physically remove block
		_blocks.remove(svgBlock);
		blockContainer.removeChild(svgBlock.block);
		Utils.log("deleting block done");
	}

	private void deleteLines(SVGConnectionPoint sdp) {
		// first remove all references of the line from the given point
		Set<SVGConnectionLine> lines = _connPoint.remove(sdp);
		if (lines == null) return;
		for (SVGConnectionLine scl : lines) {
			// remove the reference at the other end of the line
			if (sdp instanceof SVGSourcePoint)
				_connPoint.get(scl.getDestPoint()).remove(scl);
			else
				_connPoint.get(scl.getSourcePoint()).remove(scl);
			
			// remove the physical line
			scl.removeLine();
		}
	}

	public void reAttach(SVGBuildingBlock svgBuildingBlock) {
		blockContainer.appendChild(svgBuildingBlock.block);
	}
	
	public void move(OMSVGGElement elem, float x, int y) {
		OMSVGMatrix matrix = canvas.createSVGMatrix().translate(x, y);
		OMSVGTransform tr = elem.getTransform().getBaseVal().createSVGTransformFromMatrix(
				matrix);
		elem.getTransform().getBaseVal().appendItem(tr);
	}

	/**
	 * Called when the user requests to connect two points to each other.
	 */
	public void connect(final SVGSourcePoint sourcePoint, final SVGDestPoint destPoint) {
		makePendingLine(_tempLine);
		Utils.log("Got new line creation...");
		
		ServerSession.get().makeConnection(sourcePoint.getBBIName(), sourcePoint.getPointName(), 
				destPoint.getBBIName(), destPoint.getPointName(),
				new ResponseTask<BuildingBlockInstanceOverlay>() {
			@Override
			public void run(BuildingBlockInstanceOverlay json) {
				removeTempLine();
				makeLine(sourcePoint, destPoint);
				ServerSession.get().notifyChange();
			}
			
			@Override
			public void failed(Throwable exception, String debugDescription) {
//				Window.alert("Failed:"  + exception.getMessage());
				removeTempLine();
			}
		}, "create line");
//		Window.alert("Points: " + sp.getX() + " - " + sp.getY() + " - " + dp.getX() + " - " +dp.getY());
	}
	
	private SVGDestPoint findDestPoint(String blockName, String destPoint) {
		SVGBuildingBlock sbb = findBlock(blockName);
		if (sbb == null)
			return null;
		
		for (SVGDestPoint sdp : sbb.getDestPoints()) {
			if (sdp.getPointName().equals(destPoint))
				return sdp;
		}
		return null;
	}

	private SVGSourcePoint findSourcePoint(String blockName,
			String sourcePoint) {
		SVGBuildingBlock sbb = findBlock(blockName);
		if (sbb == null)
			return null;
		
		for (SVGSourcePoint ssp : sbb.getSourcePoints()) {
			if (ssp.getPointName().equals(sourcePoint))
				return ssp;
		}
		return null;
	}
	
	private SVGBuildingBlock findBlock(String blockName) {
		for (SVGBuildingBlock block : _blocks) {
			if (block.getInstance().getName().equals(blockName)) {
				return block;
			}
		}
		return null;
	}

	private void removeTempLine() {
		// remove temp _line
		lineContainer.removeChild(_tempLine);
		_tempLine = null;
	}
	
	private void makePendingLine(OMSVGLineElement tempLine2) {
		_tempLine.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_VALUE, "#FFAA00");
	}

	private OMSVGPoint getPosition(OMSVGGElement sourcePoint) {
		if (sourcePoint.getElement().getParentNode() == null) {
			throw new IllegalStateException("must be added to the tree first");
		}
		OMSVGPoint p1 = canvas.createSVGPoint(
				sourcePoint.getBBox().getCenterX(),
				sourcePoint.getBBox().getCenterY());
		return  p1.matrixTransform(sourcePoint.getTransformToElement(canvas));
	}

	private void addLinePoint(SVGConnectionPoint point,
			SVGConnectionLine line) {
		if (!_connPoint.containsKey(point)) {
			_connPoint.put(point, new HashSet<SVGConnectionLine>());
		}
		_connPoint.get(point).add(line);	
	}

	/**
     * Returns the coordinates of a mouse event, converted
     * to the SVG coordinate system
     * @param e
     * A mouse event
     * @return
     * The coordinates of the mouse event, converted
     * to the SVG coordinate system
     */
    public OMSVGPoint getLocalCoordinates(float x, float y) {
        OMSVGPoint p = canvas.createSVGPoint(x,y);
        OMSVGMatrix m = canvas.getScreenCTM().inverse();
        return p.matrixTransform(m);
    }

	public void updateSrc(SVGConnectionPoint p) {
		Set<SVGConnectionLine> lines = _connPoint.get(p);
		if (lines == null) return;
		for (SVGConnectionLine line : lines) {
			Helpers.updateStart(line.getLine(), getPosition(p.getSVGObject()));
//			Helpers.centerText(line.getText(), line.getLine());
		}
	}

	public void updateDest(SVGConnectionPoint p) {
		Set<SVGConnectionLine> lines = _connPoint.get(p);
		if (lines == null) return;
		for (SVGConnectionLine line : lines) {
			Helpers.updateEnd(line.getLine(), getPosition(p.getSVGObject()));
//			Helpers.centerText(line.getText(), line.getLine());
		}	
	}
	
	public void startTempLine(SVGConnectionPoint svgConnectionPoint,
			OMSVGPoint mouseLocation) {
		_tempPoint = svgConnectionPoint;
		if (svgConnectionPoint.isSource()) {
			_tempLine = Helpers.createConnectionLine(getPosition(svgConnectionPoint.getSVGObject()), mouseLocation);
		} else {
			_tempLine = Helpers.createConnectionLine(mouseLocation, getPosition(svgConnectionPoint.getSVGObject()));
		}
		lineContainer.appendChild(_tempLine);
	}
	
	public boolean lineBusy() {
		return _tempLine != null;
	}
	
	public void endTempLine(SVGConnectionPoint endPoint) {
		if (!lineBusy()) return;
		
		if (endPoint == null) {
			removeTempLine();
		} else if (_tempPoint.isSource() && !endPoint.isSource()) {
			connect((SVGSourcePoint) _tempPoint, (SVGDestPoint) endPoint);
		} else if (!_tempPoint.isSource() && endPoint.isSource()){
			connect((SVGSourcePoint) endPoint, (SVGDestPoint) _tempPoint);
		} else {
			removeTempLine();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
//		OMSVGPoint mouseLocation = getLocalCoordinates(event.getClientX(), event.getClientY());
		if (_groupCreationInProgress) {
//			BlockGroup b = 
			BlockGroup.create(_tempGroupBox.getBBox());  
			lineContainer.removeChild(_tempGroupBox);
			
			_groupCreationInProgress = false;
		} else {
			endTempLine(null);	
//		tempLine = null;
		}
		
		// canvas sinks all clicks...
		event.stopPropagation();
		event.preventDefault();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		OMSVGPoint p = getLocalCoordinates(event.getClientX(), event.getClientY());
		if (_groupCreationInProgress) {
			updateGroupBox(p);	
		} else {
			updateTempLine(p);
		}
	}

	private void updateGroupBox(OMSVGPoint p) {
		if (_tempGroupBox == null) return;
		_tempGroupBox.getWidth().getBaseVal().setValue(p.getX() - _groupCreationStart.getX());
		_tempGroupBox.getHeight().getBaseVal().setValue(p.getY() - _groupCreationStart.getY());
	}

	public void updateTempLine(OMSVGPoint p) {
		if (_tempLine == null) return;
		
		if (_tempPoint.isSource()) {
			Helpers.updateEnd(_tempLine, p);
		} else {
			Helpers.updateStart(_tempLine, p);
		}
	}

	/**
	 * Finds a source point relatively close to the given object. 
	 * There may not be any point close, then it returns null.
	 * 
	 * @param sourceObj
	 * @return
	 */
	public SVGSourcePoint getOverlappingSourcePoint(OMSVGGElement sourceObj) {
		OMSVGPoint point = getPosition(sourceObj);
		for (SVGBuildingBlock sbb : _blocks) {
			for (SVGSourcePoint scp : sbb.getSourcePoints()) {
				OMSVGPoint p2 = getPosition(scp.sourcePoint);
				if (p2.distance2(point) < MAX_DIST_SOURCE_POINT_2) {
					return scp;
				}
			}
		}
		return null; // found nothing
	}
	
	/* lines */

	protected void makeLine(ConnectionOverlay co) {
		SVGSourcePoint sourcePoint = findSourcePoint(co.getSourceBlock(), co.getSourcePoint());
		SVGDestPoint destPoint = findDestPoint(co.getDestBlock(), co.getDestPoint());
		makeLine(sourcePoint, destPoint);
	}

	/** 
	 * Draws a graphical line between a source and a destination,
	 * and makes sure the line gets redrawn when one of the blocks is moved.
	 * Also, it makes sure the line is deleted when the line is clicked.
	 */
	protected void makeLine(SVGSourcePoint sourcePoint, SVGDestPoint destPoint) {
		OMSVGPoint sp = getPosition(sourcePoint.getSVGObject());
		OMSVGPoint dp = getPosition(destPoint.getSVGObject());
		
		SVGConnectionLine svgLine = new SVGConnectionLine(lineContainer, 
				sp, dp, destPoint.getBBIName(), destPoint.getPointName(),
				sourcePoint, destPoint, this);
		
		/* store the points, so that the lines can be updated. */
		addLinePoint(sourcePoint, svgLine);
		addLinePoint(destPoint, svgLine);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// start group create
//		_groupCreationInProgress = true;
//		_groupCreationStart = getLocalCoordinates(event.getClientX(), event.getClientY());
//		
//		_tempGroupBox = Helpers.createTempGroupBox(_groupCreationStart);
////	tempGroupBox = canvas.createSVGRect(_groupCreationStart.getX(), _groupCreationStart.getY(), 0, 0);
//		lineContainer.appendChild(_tempGroupBox);
		
		// canvas sinks all clicks...
		event.stopPropagation();
		event.preventDefault();

	}
	
	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

	@Override
	public void removeLine(SVGConnectionLine line) {
		// remove all references to a line.
		for (Set<SVGConnectionLine> lines : _connPoint.values()) {
			lines.remove(line);
		}
		line.removeLine();
	}
}
