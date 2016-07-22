package com.spinque.gwt.client.editor.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ValueBox;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.ControlInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ControlOverlay;

/**
 * enable/disable a parameter
 * - edit each parameter (name, title, group(cluster), default value).

 * If user configurable:
 * - name, title, group, default value
 * 
 * If not user configurable:
 * - fixed value
 */
public class ParameterControlEditPanel extends Composite implements ValueChangeHandler, KeyUpHandler {
	// TODO: perhaps this pane can be docked or pinned to the 
	// display somewhere (so it doesn't need to be opened each
	// time.
	
	interface MyUiBinder extends UiBinder<Widget, ParameterControlEditPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField SpanElement paramSpan;
	@UiField SpanElement typeSpan;
	@UiField CheckBox configurableCheckBox;
	
	@UiField DeckPanel contentDeck;
	@UiField HTMLPanel configurablePane;
	@UiField HTMLPanel fixedPane;
	
	// configurable panel
	@UiField TextBox nameTextBox;
	@UiField TextBox titleTextBox;
	@UiField TextBox clusterTextBox;

	@UiField (provided=true) ValueBox defaultValueBox;
	
	@UiField TextBox formatOptionsTextBox;
	
	// non-configurable panel
	@UiField TextBox fixedValueTextBox;

	private ControlInstanceOverlay _originalValue;
	private final ControlOverlay _co;

	private BuildingBlockEditPanel _panel;
	
	ParameterControlEditPanel(String bbiName, ControlOverlay co, ControlInstanceOverlay spo, BuildingBlockEditPanel panel) {
		_panel = panel;
		_co = co;
		if (_co != null && _co.getList() != null) {
			String list = _co.getList();
			if (list.startsWith("fixed:")) {
				String valuesStr = list.substring("fixed:".length());
				String[] values = valuesStr.split("\\|");
				defaultValueBox = new ValueBox();
				defaultValueBox.setOptionValues(values);
			} else {
				defaultValueBox = new ValueBox();
			}
		} else {
			defaultValueBox = new ValueBox();
		}
		
		initWidget(uiBinder.createAndBindUi(this));
		
		_originalValue = spo;
		paramSpan.setInnerText(spo.getControl());
		typeSpan.setInnerText(spo.getDataType());
		
		/* set initial pane */
		setUserConfigurable(spo.isUserConfigurable());
		configurableCheckBox.setValue(spo.isUserConfigurable());

		nameTextBox.setValue(spo.getName());
		titleTextBox.setValue(spo.getTitle());
		clusterTextBox.setValue(spo.getCluster());
		defaultValueBox.setValue(spo.getValue());
		formatOptionsTextBox.setValue(spo.getFormatOptions());
		fixedValueTextBox.setValue(spo.getValue());
		
		configurableCheckBox.setVisible(false);
	}
	
	private List<HandlerRegistration> regs = new ArrayList<HandlerRegistration>();
	@Override
	protected void onLoad() {
		regs.add(configurableCheckBox.addValueChangeHandler(this));
		regs.add(nameTextBox.addValueChangeHandler(this));
		regs.add(nameTextBox.addKeyUpHandler(this));
		regs.add(titleTextBox.addValueChangeHandler(this));
		regs.add(titleTextBox.addKeyUpHandler(this));
		regs.add(clusterTextBox.addValueChangeHandler(this));
		regs.add(clusterTextBox.addKeyUpHandler(this));
		regs.add(defaultValueBox.addValueChangeHandler(this));
		regs.add(defaultValueBox.addKeyUpHandler(this));
		regs.add(formatOptionsTextBox.addValueChangeHandler(this));
		regs.add(formatOptionsTextBox.addKeyUpHandler(this));
		regs.add(fixedValueTextBox.addValueChangeHandler(this));
		regs.add(fixedValueTextBox.addKeyUpHandler(this));
	}
	@Override
	protected void onUnload() {
		for (HandlerRegistration reg : regs) {
			reg.removeHandler();
		}
		regs.clear();
	}
	
	/**
	 * Determines whether the record has been changed during this objects lifetime. 
	 * @return
	 */
	public boolean hasChanged() {
		if (_originalValue.isUserConfigurable() != configurableCheckBox.getValue())
			return true;
		
		if (configurableCheckBox.getValue())
			return !( 
				_originalValue.getValue().equals(defaultValueBox.getValue())
			);
		else 
			return !_originalValue.getValue()
						.equals(fixedValueTextBox.getValue());
	}
	
	@UiHandler("configurableCheckBox")
	void onUserConfigurableChange(ValueChangeEvent<Boolean> event) {
		setUserConfigurable(event.getValue());
	}
	
	void setUserConfigurable(boolean userConfigurable) {
		if (userConfigurable) { // copy value
			defaultValueBox.setValue(fixedValueTextBox.getValue());
			contentDeck.showWidget(contentDeck.getWidgetIndex(configurablePane));
		} else {
			fixedValueTextBox.setValue(defaultValueBox.getValue());
			contentDeck.showWidget(contentDeck.getWidgetIndex(fixedPane));
		}
	}

	public JSONObject getChangeString() {
		JSONObject jo = new JSONObject();
		jo.put("paramName", new JSONString(_originalValue.getControl()));
		jo.put("configurable", JSONBoolean.getInstance(configurableCheckBox.getValue()));
		if (configurableCheckBox.getValue()) {
			jo.put("name", new JSONString(nameTextBox.getValue()));
			jo.put("title", new JSONString(titleTextBox.getValue()));
			jo.put("cluster", new JSONString(clusterTextBox.getValue()));
			jo.put("format", new JSONString(formatOptionsTextBox.getValue()));
			jo.put("defaultValue", new JSONString(defaultValueBox.getValue()));
		} else {
			jo.put("defaultValue", new JSONString(fixedValueTextBox.getValue()));
		}
		return jo;
	}

	public void setFocus() {
		// FIXME: set tab index
		if (configurableCheckBox.getValue()) {
			nameTextBox.setFocus(true);
		} else {
			fixedValueTextBox.setFocus(true);
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		_panel.markChanged(this, hasChanged());
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		_panel.markChanged(this, hasChanged());
	}
	public String getName() {
		return _co.getControl();
	}
	public String getValue() {
		if (configurableCheckBox.getValue()) {
			return defaultValueBox.getValue();
		} else {
			return fixedValueTextBox.getValue();
		}
	}

//	private JComboBox createListControl(final LogicalStrategyGraph logical, final ParameterControl control, final String bbiName) {
//		final JComboBox combo = new JComboBox();
//		combo.setMaximumRowCount(5);
//		switch (control.getListGenerator().getMethod()) {
//		case fixed:
//			for (String i : _control.getListGenerator().getRawValueDescription().split("\\|")) {
//				combo.addItem(i);
//			}
//			combo.setEditable(false);
//			break;
//		case fromSummary:
//			FocusListener fl = new FocusListener() {
//				@Override
//				public void focusLost(FocusEvent e) {
//					/* do nothing */
//					String value = (String) combo.getSelectedItem();
//					if (value != null && !value.equals(_currentText)) {
//						_currentText = value;
//						if(_listener.shouldTrigger())
//							_listener.notifyParameterChange(bbiName, new Parameter(_control, value));	
//					}
//				}
//				
//				@Override
//				public void focusGained(FocusEvent e) {
//					try {
//						SummaryValueDescription opts = control.getListGenerator().getValueDescription();
//						List<String> options = logical.getStringParamOptionsFor(_bbiName, opts);
//						combo.removeAllItems();
//						if (options != null && !options.isEmpty()) {
////							combo.setEditable(false);
//							for (String option : options) {
//								combo.addItem(option);
//							}
//							combo.revalidate();
//						} else {
////							combo.setEditable(true);
//							combo.revalidate();
//						}
//						
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
//			};
//			combo.addFocusListener(fl);
//			combo.setEditable(true);
//			/* make sure the editor component also listens to focus, otherwise it won't work */
//			combo.getEditor().getEditorComponent().addFocusListener(fl);
//			break;
//		}
//		return combo;
//	}
}
