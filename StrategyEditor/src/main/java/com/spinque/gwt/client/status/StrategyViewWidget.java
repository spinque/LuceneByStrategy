package com.spinque.gwt.client.status;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class StrategyViewWidget extends Composite {

	interface MyUiBinder extends UiBinder<Widget, StrategyViewWidget> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField HTMLPanel panel;
	@UiField DivElement descriptionDiv;
	@UiField PreElement codeDiv;
	
	@UiField CodeStyle style;
	
	public StrategyViewWidget(String xmlString) {
		initWidget(uiBinder.createAndBindUi(this));
		descriptionDiv.setInnerText("Strategy in XML: this code can be executed using the lucene-execute-app");
		prepareCode(xmlString, codeDiv);
	}

	private void prepareCode(String code, Element div) {
		Document d = Document.get();
		int pos = 0;
		while (true) {
			// replace specific occurences of %...% with widget
			int nextPos = code.indexOf('%', pos);
			if (nextPos == -1)
				break;
			div.appendChild(d.createTextNode(code.substring(pos, nextPos)));
			pos = replaceToken(div, code, nextPos);
		}
		div.appendChild(d.createTextNode(code.substring(pos)));
	}
	
	public interface CodeStyle extends CssResource {
		String io();
		String original();
//		String original2();
		String variable();
	}

	private int replaceToken(Element div, String code, int pos) {
		pos++; /* skip the starting '%' */
		int nextPos = code.indexOf("%", pos);
		if (nextPos == -1)
			return code.length();
		
		String token = code.substring(pos, nextPos);
		
		if (token.equals("INSTANCE")) {
			/* a param..., it can just be stripped, do nothing */
		} else if (token.startsWith("INSTANCE:")) {
			/* it's an input (or output??) */
			String name = token.substring("INSTANCE:".length());
			SpanElement result = Document.get().createSpanElement();
			result.setInnerHTML(name);
			result.setClassName(style.io());
			div.appendChild(result);
		} else if (token.startsWith("ORIGINAL:")) {
			String name = token.substring("ORIGINAL:".length());
			
//			String[] tokens = name.split(":", 2);
				
			SpanElement result = Document.get().createSpanElement();
			result.setInnerHTML(name); // tokens[0]);
			result.setClassName(style.original());
			div.appendChild(result);
			
//			if (tokens.length == 2) {
//				SpanElement result2 = Document.get().createSpanElement();
//				result2.setInnerHTML(tokens[1]);
//				result2.setClassName(style.original2());
//				div.appendChild(result2);
//			}
		} else {
			/* It's a variable... */
			SpanElement result = Document.get().createSpanElement();
			result.setInnerHTML('%' + token + '%');
			result.setClassName(style.variable());
			div.appendChild(result);
			
		}
		return nextPos+1;
	}

}
