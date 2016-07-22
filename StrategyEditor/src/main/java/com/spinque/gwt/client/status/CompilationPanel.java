package com.spinque.gwt.client.status;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.rest.client.NotificationOverlayException;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.NotificationOverlay;
import com.spinque.rest.client.overlay.NotificationOverlay.StackTraceOverlay;

public class CompilationPanel extends PhasePanel {
	
	private final static int MAX_LENGTH = 100;
	
	CompilationPanel() {
		super("2. Compilation", true);
		setAutoUpdate(true);
	}

	public void doUpdate() {
		doCompile();
	}

	private int _id;

	private void doCompile() {
		setPanelState(WAITING);
		
		ServerSession.get().compile( 
				new ResponseTask<NotificationOverlay>() {

			@Override
			public void run(final NotificationOverlay json) {
				contentsPanel.clear();
				Button b = new Button("Generate XML");
				contentsPanel.add(b);
				b.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						DialogBox db = new DialogBox(true);
						db.setText("Strategy XML");
						db.setWidget(new StrategyViewWidget(json.getMessage()));
						db.center();
					}
				});
				setPanelState(OK);
//				next().signalChange(true);
			}

			@Override
			public void failed(Throwable exception, String debugDescription) {
				setPanelState(ERROR);
				contentsPanel.clear();
				if (exception instanceof NotificationOverlayException) {
					contentsPanel.add(makeExceptionWidget((NotificationOverlayException)exception, debugDescription));
				} else {
					contentsPanel.add(new Label("internal error: " + debugDescription));
				}
//				next().signalChange(false);
			}
		}, "compile");
	}

	private static Widget makeExceptionWidget(NotificationOverlayException exception, String debugDescription) {
		String message = debugDescription + ":" + exception.getMessage();
		JsArray<StackTraceOverlay> data = exception.getNotification().getTrace();
		if (data != null && data.length() > 0) {
			StackTraceOverlay sto = data.get(data.length()-1);
			message = sto.getMessage();
		}
		if (message.length() > MAX_LENGTH + 2)
			message = message.substring(0, MAX_LENGTH) + "...";
		return new Label(message);
	}
}
