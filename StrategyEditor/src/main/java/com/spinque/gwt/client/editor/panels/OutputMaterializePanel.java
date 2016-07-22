package com.spinque.gwt.client.editor.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.gwt.client.editor.primitives.SVGBuildingBlock;
import com.spinque.gwt.client.editor.primitives.SVGSourcePoint;
import com.spinque.gwt.utils.client.dialog.ChangedHandler;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel.PopupContents;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.NotificationOverlay;

/**
 * Provides options related to a specific block output:
 * 
 * - materialize (yes/no) : manual optimization option.
 *  
 */
public class OutputMaterializePanel extends Composite implements PopupContents {
	
	interface MyUiBinder extends UiBinder<Widget, OutputMaterializePanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	 
//	@UiField SpanElement nameSpan;
	@UiField CheckBox materializeCheckBox;
	
	private final SVGBuildingBlock _svgBuildingBlock;
	private final SVGSourcePoint _ssp;

	private ChangedHandler _handler;
	
	public OutputMaterializePanel(SVGBuildingBlock svgBuildingBlock, SVGSourcePoint ssp) {
		initWidget(uiBinder.createAndBindUi(this));
		
		_svgBuildingBlock = svgBuildingBlock;
		_ssp = ssp;
//		nameSpan.setInnerText(_svgBuildingBlock.getInstance().getBuildingBlock() + " - " + ssp.getPointName());
		materializeCheckBox.setValue(ssp.getMaterialize());
	}
	
	@UiHandler("materializeCheckBox")
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (_handler != null) {
			_handler.changedEvent(_ssp.getMaterialize() != materializeCheckBox.getValue());
		}
	}

	@Override
	public Widget getContent() {
		return this;
	}

	@Override
	public void applyChanges(final DialogContentsPanel pp) {
		final boolean materialize = materializeCheckBox.getValue();
		ServerSession.get().makeRequest("materialize", 
				"/" + _svgBuildingBlock.getInstance().getName() + "/" + _ssp.getPointName() + "/" + materialize, null, 
				new ResponseTask<NotificationOverlay>() {

					@Override
					public void run(NotificationOverlay json) {
						if (json.getStatus().equals("OK")) {
							_ssp.setMaterialize(materialize);
						}
						pp.close();
					}

					@Override
					public void failed(Throwable exception, String debugDescription) {
					}
			}, "change materialize state");
	}

	public static OutputMaterializePanel generate(
			SVGBuildingBlock svgBuildingBlock, SVGSourcePoint ssp) {
		return new OutputMaterializePanel(svgBuildingBlock, ssp);
	}

	@Override
	public HandlerRegistration addChangedHandler(ChangedHandler handler) {
		_handler = handler;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				_handler = null;
			}
		};
	}
}
