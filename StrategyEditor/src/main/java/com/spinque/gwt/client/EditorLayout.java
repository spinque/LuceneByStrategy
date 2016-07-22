package com.spinque.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.library.LibraryBuildingBlockPanel;
import com.spinque.gwt.client.editor.primitives.StrategyCanvas;
import com.spinque.gwt.client.status.StrategyDebugPanel;

public class EditorLayout extends ResizeComposite {

	interface MyUiBinder extends UiBinder<Widget, EditorLayout> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField DockLayoutPanel root;
	
	@UiField(provided=true) StrategyDebugPanel status;
	@UiField(provided=true) StrategyCanvas canvas;
	@UiField(provided=true) LibraryBuildingBlockPanel library;
	
	public EditorLayout(
			StrategyDebugPanel strategyStatus, 
			StrategyCanvas strategyCanvas, 
			LibraryBuildingBlockPanel libraryBuildingBlockPanel) {
		status = strategyStatus;
		canvas = strategyCanvas;
		library = libraryBuildingBlockPanel; 
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
}
