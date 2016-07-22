package com.spinque.gwt.client.editor.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;

public class LibraryBuildingBlockCell extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, LibraryBuildingBlockCell> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private final BuildingBlockOverlay _bb;

	@UiField FocusPanel panel;
//	@UiField DivElement detailsDiv;
	@UiField SpanElement nameSpan;
	@UiField Image img;
	@UiField SpanElement infoButton; 
	
	/**
	 * The html of the image used for contacts.
	 */
	public LibraryBuildingBlockCell(BuildingBlockOverlay value) {
		initWidget(uiBinder.createAndBindUi(this));
		_bb = value;
		nameSpan.setInnerText(value.getName());
		getElement().setAttribute("block", "yes");
//		detailsDiv.setInnerText("Name: " + value.getName());
	}

	public LibraryBuildingBlockCell cloneWidget() {
		return new LibraryBuildingBlockCell(_bb);
	}

	public BuildingBlockOverlay getBuildingBlockOverlay() {
		return _bb;
	}
	
	public void showBlockInfo() {
		DialogBox db = new DialogBox(true);
		db.setText("Building block: " + _bb.getName());
		db.setWidget(new CodeViewWidget(_bb));
		db.center();
	}
}
