package com.spinque.gwt.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.gwt.client.editor.library.BlockLoader;
import com.spinque.gwt.client.editor.library.LibraryBuildingBlockPanel;
import com.spinque.gwt.client.editor.primitives.StrategyCanvas;
import com.spinque.gwt.client.status.StrategyDebugPanel;
import com.spinque.gwt.utils.client.DebugUtils;
import com.spinque.gwt.utils.client.SpinqueEntryPoint;
import com.spinque.rest.client.ConfigResource;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockCategoryOverlay;
import com.spinque.rest.client.overlay.WorkspaceDetailsOverlay;

public class EditorApplication implements EntryPoint, SpinqueEntryPoint {	
	
	public static final String APPBASE = ConfigResource.INSTANCE.getRestURL();
	public static final boolean DEBUG_MODE = ConfigResource.INSTANCE.getDebugMode();
	public static final boolean DEMO_MODE = ConfigResource.INSTANCE.getDemoMode();
	public static final boolean EDUCATION_MODE = ConfigResource.INSTANCE.getEducationMode();
	
	private static final long COOKIE_DURATION = 1000L * 60L * 60L; /* 1 hour */
	
	@Override
	public void onModuleLoad() {
		if (DEBUG_MODE) {
			DebugUtils.wrapDebug(this);
		} else {
			load();
		} 
	}
	
	public static PickupDragController dragController = null;

	@Override
	public void load() {
		Window.setTitle("Strategy Editor");
		
		String strategyParam = Window.Location.getParameter("strategy");
		final String strategy = (strategyParam != null && strategyParam.trim().isEmpty()) ? null : strategyParam;
		
		String queryString = Window.Location.getParameter("queryString");
		
		Window.setTitle("LuceneByStrategy Editor");

	    final StrategyDebugPanel strategyStatus = new StrategyDebugPanel();
		final StrategyCanvas strategyCanvas = new StrategyCanvas();
		
		EditorLayout el = new EditorLayout(strategyStatus, strategyCanvas, createLibrary());
		RootLayoutPanel.get().add(el);
		
		String sessionID = "abc";
		ServerSession.initialize(sessionID, "none", 
				new FinalInit(strategy, strategyCanvas, strategyStatus));
	}

	static class FinalInit implements ResponseTask<WorkspaceDetailsOverlay> {
		private StrategyCanvas _strategyCanvas;
		private String _strategy;
		private StrategyDebugPanel _strategyStatus;

		public FinalInit(String strategy, StrategyCanvas strategyCanvas, 
				StrategyDebugPanel strategyStatus) {
			_strategy = strategy;
			_strategyCanvas = strategyCanvas;
			_strategyStatus = strategyStatus;
		}

		@Override
		public void run(WorkspaceDetailsOverlay json) {
			_strategyCanvas.initSession(new Callback<Void, Throwable>() {
				
				@Override
				public void onSuccess(Void result) {
					_strategyStatus.initSession();
					ServerSession.get().addListener(_strategyStatus);
				}
				
				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Failed to initialize: " + reason.getMessage());
				}
			});
		}

		@Override
		public void failed(Throwable exception, String debugDescription) {
			Window.alert("Failed to initialize: " + exception.getMessage());
		}
	}
	
	private LibraryBuildingBlockPanel createLibrary() {
		BlockLoader bl = BlockLoader.INSTANCE;
		final LibraryBuildingBlockPanel lbbp = new LibraryBuildingBlockPanel(bl);
		
		for (BuildingBlockCategoryOverlay bbco : bl.getCategories()) {
			lbbp.addCategory(bbco);	
		}
		
		return lbbp;
	}

	public static PickupDragController prepareDragController(AbsolutePanel panel) {
		// create a DragController to manage drag-n-drop actions
	    // note: This creates an implicit DropController for the boundary panel
		if (EditorApplication.dragController != null)
			return EditorApplication.dragController;
	    EditorApplication.dragController = new PickupDragController(panel, true) {
	    	@Override
	    	protected Widget newDragProxy(DragContext context) {
	    		AbsolutePanel container = new AbsolutePanel();
	    		container.getElement().getStyle().setProperty("overflow", "visible");

	    		WidgetArea draggableArea = new WidgetArea(context.draggable, null);
	    		for (Widget widget : context.selectedWidgets) {
	    			WidgetArea widgetArea = new WidgetArea(widget, null);
	    			Widget proxy = new SimplePanel(); // FIXME: replace SimplePanel with a shape of the BB.
	    			proxy.setPixelSize(widget.getOffsetWidth(), widget.getOffsetHeight());
	    			proxy.addStyleName(DragClientBundle.INSTANCE.css().proxy());
	    			container.add(proxy, widgetArea.getLeft() - draggableArea.getLeft(),
	    					widgetArea.getTop() - draggableArea.getTop());
	    		}
	    		return container;
	    	}
	    };
	    EditorApplication.dragController.setBehaviorMultipleSelection(false);
	    EditorApplication.dragController.setBehaviorDragProxy(true);
	    EditorApplication.dragController.setBehaviorConstrainedToBoundaryPanel(true);
	    EditorApplication.dragController.setBehaviorDragStartSensitivity(5);
	    return EditorApplication.dragController;
	}
}
