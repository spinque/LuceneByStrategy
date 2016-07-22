package com.spinque.gwt.utils.client.dialog;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel.PopupContents;

public class SpinqueDialogBox extends DialogBox {
	
	public SpinqueDialogBox(PopupContents bbep, String caption, int buttons) {
		super(true, true);
		setAnimationEnabled(true);
		setText(caption);
		DialogContentsPanel dcp = new DialogContentsPanel(this, bbep, buttons);
		setWidget(dcp);
	}

	public void doShow() {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = Window.getScrollLeft() + (Window.getClientWidth() - offsetWidth) / 3;
				int top = Window.getScrollTop() + (Window.getClientHeight() - offsetHeight) / 3;
				setPopupPosition(left, top);
			}
		});
	}
}