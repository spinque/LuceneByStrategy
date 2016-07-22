package com.spinque.gwt.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class PhasePanel extends Composite {
	
	public final static int WAITING = 0;
	public final static int UNKNOWN = -1;
	public final static int OK = 1;
	public final static int ERROR = 2;
	
	interface MyUiBinder extends UiBinder<Widget, PhasePanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PhasePanel _next = null;
	
	@UiField HTMLPanel disclosurePanel;
	@UiField FlowPanel contentsPanel;
	@UiField PushButton refreshButton;
	@UiField SpanElement titleSpan;
	@UiField CheckBox autoUpdateCheckbox;
	@UiField HTMLPanel busyState;
	@UiField HTMLPanel uninitializedState;
	@UiField DeckPanel contentDeck;
	@UiField HTMLPanel header;
	private final boolean _openOnOK;
	private PhasePanel _prev;
	
//	private boolean _state = true;
	
	@UiHandler("refreshButton")
	public void onRefreshButtonClick(ClickEvent event) {
		if (_state == WAITING)
			return;
		
		if (_next != null) next().doRollUp();
		setPanelState(UNKNOWN);
		
		if (_prev == null || _prev.isOK())
			doUpdate();
	}
	
	private int _state = -1;
	
	private boolean isOK() {
		return _state == OK;
	}

	public PhasePanel(String title, boolean openOnOK) {
		initWidget(uiBinder.createAndBindUi(this));	
		titleSpan.setInnerText(title);
		contentDeck.showWidget(0);
		_openOnOK = openOnOK;
	}
	
	void setNeighbourPanels(PhasePanel prev, PhasePanel next) {
		_next = next;
		_prev = prev;
	}
	
	PhasePanel next() {
		return _next;
	}
	PhasePanel prev() {
		return _prev;
	}
	
	void doRollUp() {
		setPanelState(UNKNOWN);
		if (_next != null) _next.doRollUp();
	}
	
	void setOpen(boolean state) {
		contentDeck.getElement().getStyle().setDisplay(state ? Display.BLOCK : Display.NONE);
	}
	
	public void initialize() {
		// to be overriden.
	}
	
	// unknown == grey
	// ok = green
	// error = red
	// waiting = grey?
	void setPanelState(int state) {
		_state = state;
		switch (state) {
		case UNKNOWN:
			header.addStyleDependentName("unknown");
			header.getElement().getStyle().setBackgroundColor("#EEEEEE");
			contentDeck.showWidget(0);
			setOpen(false);
			break;
		case WAITING:
			header.addStyleDependentName("waiting");
			header.getElement().getStyle().setBackgroundColor("#F7F7DD");
			contentDeck.showWidget(1);
			setOpen(true);
			break;
		case ERROR:
			header.addStyleDependentName("error");
			header.getElement().getStyle().setBackgroundColor("#F7DDDD");
			contentDeck.showWidget(2);
			setOpen(true);
			break;
		case OK:
			header.addStyleDependentName("ok");
			header.getElement().getStyle().setBackgroundColor("#DDF7DD");
			contentDeck.showWidget(2);
			setOpen(_openOnOK);
			break;
		}
	}

	public void signalChange(boolean canCalculate) {
		if (_next != null) next().doRollUp();
		setPanelState(UNKNOWN);
		
		if (canCalculate && autoUpdateCheckbox.getValue()) {
			doUpdate();
		}
	}
	
	public void setAutoUpdate(boolean autoUpdate) {
		autoUpdateCheckbox.setValue(autoUpdate);
	}

	public void doUpdate() {
		// override this method
		// when updated, call _next.signalChange() (if _next is defined)
	}

//	public void signalChange() {
//		// delay update a little
//		if (_lastUpdate > System.currentTimeMillis() - UPDATE_DELAY) {
//			if (!_scheduled && !_inProgress) {
//				_scheduled  = true;
//				_t.schedule(Math.max(200, Math.min(50, (int) (_lastUpdate + UPDATE_DELAY - System.currentTimeMillis()))));
//			}
//		}
//		if (_scheduled || _inProgress)
//			return;
//		
//		_lastUpdate = System.currentTimeMillis();
//		doUpdate();
//		_scheduled = false;
//	};
//	long _lastUpdate = 0;
//	private boolean _scheduled = false;
//	Timer _t = new Timer() {
//		@Override
//		public void run() {
//			_lastUpdate = System.currentTimeMillis();
//			doUpdate();
//			_scheduled = false;
//		}
//	};
//	
}
