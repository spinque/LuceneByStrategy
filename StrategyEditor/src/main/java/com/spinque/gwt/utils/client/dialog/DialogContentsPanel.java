package com.spinque.gwt.utils.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DialogContentsPanel extends Composite implements ChangedHandler {

	public static final int OK = 1;
	public static final int CANCEL = 2;
	public static final int APPLY = 4;
	
	
	interface MyUiBinder extends UiBinder<Widget, DialogContentsPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField HTMLPanel panel;
	@UiField DivElement contentDiv;
	@UiField Button applyButton;
	@UiField Button cancelButton;

	private PopupPanel _pp;

	private PopupContents _contents;
	
	public interface PopupContents {
		Widget getContent();

		void applyChanges(DialogContentsPanel pp);

		HandlerRegistration addChangedHandler(ChangedHandler handler);
	}
	
	public DialogContentsPanel(PopupPanel pp, PopupContents contents, int buttons) {
		initWidget(uiBinder.createAndBindUi(this));
		_pp = pp;
		_contents = contents;
		panel.add(contents.getContent(), contentDiv);
		
		enableButton(applyButton, (buttons & (APPLY | OK)) != 0);
		enableButton(cancelButton, (buttons & CANCEL) != 0);
		setApplyEnabled(false);
	}
	
	HandlerRegistration _reg;
	@Override
	protected void onLoad() {
		_reg = _contents.addChangedHandler(this);
	}
	@Override
	protected void onUnload() {
		if (_reg != null) _reg.removeHandler();
		_reg = null;
	}
	public void changedEvent(boolean hasChanged) {
		setApplyEnabled(hasChanged);
	}
	
	public void setApplyEnabled(boolean enable) {
		applyButton.setEnabled(enable);
	}
	
	private void enableButton(Widget w, boolean b) {
		if (b) {
			w.getElement().getStyle().clearDisplay();
		} else {
			w.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		close();
	}
	
	@UiHandler("applyButton")
	public void onApplyClick(ClickEvent event) {
		// apply the new state 
		_contents.applyChanges(this);
	}
	
	public void close() {
		_pp.hide();
	}
	
}

