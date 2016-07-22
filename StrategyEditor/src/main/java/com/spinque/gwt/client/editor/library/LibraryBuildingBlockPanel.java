package com.spinque.gwt.client.editor.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.spinque.rest.client.overlay.BuildingBlockCategoryOverlay;

public class LibraryBuildingBlockPanel extends Composite {
	
	interface MyUiBinder extends UiBinder<DockLayoutPanel, LibraryBuildingBlockPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField StackLayoutPanel stackPanel;
	
	@UiField DockLayoutPanel libraryPanel;
	@UiField HTMLPanel headerDiv;
//	@UiField DivElement itemsDiv;
	
	interface Style extends CssResource {
		String sectionHeader();
	}
	
//	@UiHandler("hideSpan")
//	public void onHideClick(ClickEvent event) {
//		itemsDiv.getStyle().setDisplay(Display.NONE);
//	}
//	
//	@UiHandler("showSpan")
//	public void onShowClick(ClickEvent event) {
//		itemsDiv.getStyle().setDisplay(Display.BLOCK);
//	}

	@UiField Style style;

	private BlockLoader _bl;
	
	public LibraryBuildingBlockPanel(BlockLoader bl) {
		_bl = bl;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addCategory(BuildingBlockCategoryOverlay bbco) {
		final LibraryBuildingBlockCategoryPanel lbbcp = new LibraryBuildingBlockCategoryPanel(_bl, bbco);
		
		Label header = new Label(bbco.getName() + " (" + bbco.getSize() + ")");
		header.addStyleName(style.sectionHeader());
		header.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lbbcp.initialize();
			}
		});
		if (stackPanel.getWidgetCount() == 0) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					lbbcp.initialize();
				}
			});
		}
		stackPanel.add(lbbcp, header, 25);
	}
}