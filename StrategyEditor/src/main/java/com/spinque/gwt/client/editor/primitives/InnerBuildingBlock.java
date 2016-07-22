package com.spinque.gwt.client.editor.primitives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.spinque.gwt.client.editor.panels.BuildingBlockEditPanel;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel;
import com.spinque.gwt.utils.client.dialog.SpinqueDialogBox;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.ControlInstanceOverlay;

public class InnerBuildingBlock extends Composite {

	interface MyUiBinder extends UiBinder<HTMLPanel, InnerBuildingBlock> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField HTMLPanel panel;
	@UiField DivElement headerDiv;
	@UiField DivElement detailsDiv;
	@UiField Button editButton;
	@UiField Button deleteButton;

	private BuildingBlockOverlay _bbo;
//	private BuildingBlockInstanceOverlay _bbio;

	private SVGBuildingBlock _sbb;
	
	public InnerBuildingBlock(SVGBuildingBlock sbb, BuildingBlockOverlay bbo) {
		_bbo = bbo;
//		_bbio = bbio;
		_sbb = sbb;
		initWidget(uiBinder.createAndBindUi(this));
		headerDiv.setInnerText(_bbo.getName());
	}
	
	@Override
	protected void onLoad() {
		update();
	}
	
	@UiHandler("deleteButton")
	// catch edit and delete mouse down events 
	// (because otherwise they are interpreted as drag starts)
	public void onDeleteClick(ClickEvent event) {
		//						event.preventDefault();
		//						event.stopPropagation();
		if (Window.confirm("Really delete this '" + _sbb._instance.getBuildingBlock() + "' block?")) {
			_sbb.deleteBlock();
		}
		event.preventDefault();
		event.stopPropagation();
	}
	
	@UiHandler("editButton")
	public void onEditClick(ClickEvent event) {
		//						event.preventDefault();
		//						event.stopPropagation();
		doEdit();
		event.preventDefault();
		event.stopPropagation();
	}

	void doEdit() {
		BuildingBlockEditPanel bbep = BuildingBlockEditPanel.generate(_sbb);
		// FIXME: add server call here...
		final SpinqueDialogBox popup = new SpinqueDialogBox(bbep, "Edit building block properties", DialogContentsPanel.APPLY | DialogContentsPanel.CANCEL);
		popup.doShow();
	}
	
	public void update() {
		if (_sbb._instance.getControls().length() == 0) {
			editButton.setVisible(false);
			return;
		}
			
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		for (int i = 0; i < _sbb._instance.getControls().length(); i++) {
			displayControl(sb, _sbb._instance.getControls().get(i));
		}
		detailsDiv.setInnerSafeHtml(sb.toSafeHtml());
	}
	
	private void displayControl(SafeHtmlBuilder sb,
			ControlInstanceOverlay c) {
		// value)
		if (c.getTitle() != null) {
			sb.appendHtmlConstant("<div style='color: #e0a060'>");
			sb.appendEscaped(
					(c.isUserConfigurable()  
						? "C " + c.getCluster() + ":" + c.getTitle() + " (" + c.getValue() + ")"
						: "F " + c.getControl() + " = " + c.getValue()) 
					);
			sb.appendHtmlConstant("</div>");
		} else {
			sb.appendHtmlConstant("<div style='color: #a0a0a0'>");
			sb.appendEscaped("F " + c.getControl() + " = " + c.getValue());
			sb.appendHtmlConstant("</div>");
		}
//		textSpan.getStyle().setFontWeight(c.isUserConfigurable() ? FontWeight.BOLD : FontWeight.NORMAL);
//		if (!c.getUserConfigurable()) {
//			textSpan.getStyle().setColor("#AAA");
//		}
	}

}
