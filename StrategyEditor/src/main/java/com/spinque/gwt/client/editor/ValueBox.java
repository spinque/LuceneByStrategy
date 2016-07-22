package com.spinque.gwt.client.editor;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class ValueBox extends DeckPanel {
	
	private final TextBox _freeTextInput = new TextBox();
	private final ListBox _optionInput = new ListBox();
	
	public ValueBox() {
		add(_freeTextInput);
		add(_optionInput);
		showWidget(0);
	}
	
	public void setValue(String value) {
		switch (getVisibleWidget()) {
		case 0:
			_freeTextInput.setValue(value);
			break;
		case 1: 
			int selectedIndex = findIndex(value);
			_optionInput.setSelectedIndex(selectedIndex == -1 ? 0 : selectedIndex);
			break;
		}
	}

	public String getValue() {
		switch (getVisibleWidget()) {
		case 0:
			return _freeTextInput.getValue();
		case 1: 
			return getOptionValue();
		}
		return "unknoWn";
	}

	private String getOptionValue() {
		if (_optionInput.getSelectedIndex() == -1)
			return "";
		return _optionInput.getValue(_optionInput.getSelectedIndex());
	}

	public void setOptionValues(JsArrayString options) {
		String currentValue = getValue(); 
		_optionInput.clear();
		for (int i = 0; i < options.length(); i++) {
			_optionInput.addItem(options.get(i));
			if (options.get(i).equals(currentValue))
				_optionInput.setSelectedIndex(i);
		}
		showWidget(1);
	}
	
	private int findIndex(String value) {
		for (int i = 0; i < _optionInput.getItemCount(); i++) {
			if (_optionInput.getValue(i).equals(value))
				return i;
		}
		return -1;
	}

	public void setOptionValues(String[] options) {
		String currentValue = getValue(); 
		_optionInput.clear();
		for (String option : options) {
			_optionInput.addItem(option);
			if (option.equals(currentValue))
				_optionInput.setSelectedIndex(_optionInput.getItemCount() - 1);
		}
		showWidget(1);
	}
	
	/**
	 * Warning: might yield a null-event.
	 * 
	 */
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		final HandlerRegistration hr1 = _freeTextInput.addValueChangeHandler(handler);
		final HandlerRegistration hr2 = _optionInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handler.onValueChange(null);
			}
		});
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				hr1.removeHandler();
				hr2.removeHandler();
			}
		};
	}
	
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		final HandlerRegistration hr1 = _freeTextInput.addKeyUpHandler(handler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				hr1.removeHandler();
			}
		};
	}
}
