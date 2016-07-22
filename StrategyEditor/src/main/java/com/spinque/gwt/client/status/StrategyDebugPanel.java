package com.spinque.gwt.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ServerSession.StrategyEditListener;

/**
 * 
 */
public class StrategyDebugPanel extends Composite implements StrategyEditListener {
	
	interface MyUiBinder extends UiBinder<Widget, StrategyDebugPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
//	private static final long UPDATE_DELAY = 5000; // in ms
	
	@UiField FlowPanel panel;
	@UiField DockLayoutPanel statusPanel;
	
//	@UiField Anchor hideSpan;
//	@UiField Anchor showSpan;
	
	public StrategyDebugPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("hideSpan")
	public void onHideClick(ClickEvent event) {
		panel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	@UiHandler("showSpan")
	public void onShowClick(ClickEvent event) {
		panel.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	private PhasePanel correctnessPanel = new CorrectnessPanel();
	
	public void initSession() {
		PhasePanel compilationPanel = new CompilationPanel(); 
		
		/* build chain */
		correctnessPanel.setNeighbourPanels(null, compilationPanel);
		compilationPanel.setNeighbourPanels(correctnessPanel, null);
		
		panel.add(correctnessPanel);
		panel.add(compilationPanel);
		
		correctnessPanel.initialize();
		compilationPanel.initialize();
	}
	
	@Override
	public void notifyUpdate() {
		correctnessPanel.signalChange(true);
	}
}
