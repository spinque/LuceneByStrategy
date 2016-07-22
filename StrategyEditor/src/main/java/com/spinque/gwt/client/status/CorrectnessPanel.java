package com.spinque.gwt.client.status;

import com.google.gwt.user.client.Window;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.MessageOverlay;
import com.spinque.rest.client.overlay.StatusOverlay;

public class CorrectnessPanel extends PhasePanel {
	
	CorrectnessPanel() {
		super("1. Correctness", false);
		setAutoUpdate(true);
	}

	public void initialize() {
		setPanelState(UNKNOWN);
		next().doRollUp();
	}
	
	public void doUpdate() {
		doCorrectnessTest();
	}
	
	public void doCorrectnessTest() {
		setPanelState(WAITING);
					
		ServerSession.get().isStrategyCorrect( 
				new ResponseTask<StatusOverlay>() {

					@Override
					public void run(StatusOverlay json) {
						contentsPanel.clear();
						for (int i = 0; i < json.getMessages().length();i++) {
							
							MessageOverlay message = json.getMessages().get(i);
							StatusMessageItem item = new StatusMessageItem(message);
							contentsPanel.add(item);
						}
						
						if (json.getStatus().equals(StatusOverlay.OK)) {
//							statusMessageDiv.setInnerText("OK!");
//							next().doRollDown();
							setPanelState(OK);
							next().signalChange(true);
						} else if (json.getStatus().equals(StatusOverlay.ERROR)) {
//							statusMessageDiv.setInnerText("ERROR: (see items below)");
							setPanelState(ERROR);
							next().signalChange(false);
						} else if (json.getStatus().equals(StatusOverlay.SUGGESTIONS)) {
//							statusMessageDiv.setInnerText("SUGGESTIONS: (see items below)");
							setPanelState(OK);
							setOpen(true); // it is ok, but let's show some suggestions
							next().signalChange(true);
						} else if (json.getStatus().equals(StatusOverlay.PREMATURE)) {
//							statusMessageDiv.setInnerText("PREMATURE: (too early to call, see items below)");
							setPanelState(UNKNOWN);
							next().signalChange(false);
						} else if (json.getStatus().equals(StatusOverlay.WARNING)) {
//							statusMessageDiv.setInnerText("WARNING: (it will compile, but please have a look at the messages below)");
							setPanelState(ERROR);
							next().signalChange(false);
						} else {
							setPanelState(ERROR);
							next().signalChange(false);
							Window.alert("Don't know: " + json.getStatus());
						}
					}

					@Override
					public void failed(Throwable exception, String debugDescription) {
//						statusMessageDiv.setInnerText("Failed to retrieve status");
						setPanelState(ERROR);
						next().signalChange(false);
					}
			}, "status");
	}
}
