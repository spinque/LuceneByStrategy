package com.spinque.gwt.client.editor.library;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.EditorApplication;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockCategoryOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;

public class LibraryBuildingBlockCategoryPanel extends FlowPanel {

	private final BuildingBlockCategoryOverlay _bbco;
//	HandlerRegistration _hreg = null;
	private boolean _isLoaded = false;
	private final BlockLoader _bl;
//	private final List<LibraryBuildingBlockCell> cells = new ArrayList<LibraryBuildingBlockCell>();
	
	public LibraryBuildingBlockCategoryPanel(BlockLoader bl, BuildingBlockCategoryOverlay bbco) {
		_bbco = bbco;
		_bl = bl;
		getElement().getStyle().setOverflow(Overflow.AUTO);
	}

	public void initialize() {
		if (!_isLoaded) {
			final Label loadingLabel = new Label("Loading...");
			add(loadingLabel);
			_bl.fetchBlocks(_bbco.getId(), new ResponseTask<JsArray<BuildingBlockOverlay>>() {
				@Override
				public void run(JsArray<BuildingBlockOverlay> json) {
					loadingLabel.removeFromParent();
					
					// First get them sorted on name (case insensitive)
					Map <String, BuildingBlockOverlay> bbos = new TreeMap<String,BuildingBlockOverlay>(String.CASE_INSENSITIVE_ORDER);
					for (int i = 0; i < json.length(); i++) {
						BuildingBlockOverlay bbo = json.get(i);
						bbos.put(bbo.getName(), bbo);
					}
						
					for (BuildingBlockOverlay bbo : bbos.values()) {
						if (bbo.getClassName().equals(BuildingBlockOverlay.RESULT_BLOCK_CLASS))
							continue;

						ServerSession.get().addToKnownBlocks(bbo);
						LibraryBuildingBlockCell cell = new LibraryBuildingBlockCell(bbo);
						add(cell);
						add(new HTML(SafeHtmlUtils.fromSafeConstant(" "))); // spacer
						if (EditorApplication.dragController != null)
							EditorApplication.dragController.makeDraggable(cell, cell.panel);
					}
				}

				@Override
				public void failed(Throwable exception, String debugDescription) {
					// TODO Auto-generated method stub
				}
			}, "fetch blocks for " + _bbco.getId());
			_isLoaded = true;
		}
	}
	
//	@Override
//	protected void onAttach() {
//		super.onAttach();
//		for (LibraryBuildingBlockCell cell : cells) {
//			Test.dragController.makeDraggable(cell, cell.img);
//		}
//	}
//	@Override
//	protected void onDetach() {
//		super.onDetach();
//		for (LibraryBuildingBlockCell cell : cells) {
//			Test.dragController.makeNotDraggable(cell);
//		}
//	}

	/**
	 * Removed widgets that are instances of {@link PaletteWidget} are immediately replaced with a
	 * cloned copy of the original.
	 * 
	 * @param w the widget to remove
	 * @return true if a widget was removed
	 */
	@Override
	public boolean remove(Widget w) {
		int index = getWidgetIndex(w);
		if (index != -1 && w instanceof LibraryBuildingBlockCell) {
			LibraryBuildingBlockCell clone = ((LibraryBuildingBlockCell) w).cloneWidget();
			insert(clone, index);
			EditorApplication.dragController.makeDraggable(clone, clone.panel);
		}
		
		w.getElement().getStyle().setBackgroundColor("#FA0");
		return super.remove(w);
	}
}
