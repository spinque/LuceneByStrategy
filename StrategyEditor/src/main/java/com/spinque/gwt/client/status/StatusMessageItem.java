package com.spinque.gwt.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.rest.client.overlay.MessageOverlay;


/**
 * 
.panelMacro, .panel {
    margin: 0 0 10px;
}
content .infoMacro, #content .tipMacro, #content .warningMacro, #content .noteMacro, #content .panel {
    border-style: solid;
    border-width: 1px;
}
#content .infoMacro, #content .tipMacro, #content .warningMacro, #content .noteMacro, #content .panel {
    
}
.panelMacro table {
    padding: 10px;
}
.panelMacro table {
    padding: 0 20px;
    text-align: left;
    width: 100%;
}
.wiki-content, .wiki-content p, .wiki-content table, .wiki-content tr, .wiki-content td, .wiki-content th, .wiki-content ol, .wiki-content ul, .wiki-content li {
    font-size: 10pt;
    line-height: 13pt;
}
.noteMacro {
    background-color: #FFFFDD;
    border-color: #F7DF92;
}


<div class="panelMacro">
  <table class="noteMacro">
    <colgroup>
      <col width="24"><col>
    </colgroup>
    <tbody>
      <tr>
        <td valign="top"><img width="16" height="16" border="0" align="absmiddle" alt="" src="/images/icons/emoticons/warning.gif"></td>
        <td><b>Virtual Volume reservation</b><br></td>
      </tr>
    </tbody>
  </table>
</div>

 * @author alink
 *
 */
public class StatusMessageItem extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, StatusMessageItem> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField DivElement messageDiv;
	
	interface Style extends CssResource {
		String statusOK();
		String statusWarn();
		String statusError();
		String statusInfo();
	}
	
	@UiField Style style;
	
	public StatusMessageItem(MessageOverlay message) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (message.getSeverity().equals("OK")) {
			messageDiv.addClassName(style.statusOK());
		} else if (message.getSeverity().equals("WARNING")) {
			messageDiv.addClassName(style.statusWarn());
		} else if (message.getSeverity().equals("SUGGESTIONS")) {
			messageDiv.addClassName(style.statusInfo());
		} else if (message.getSeverity().equals("ERROR")) {
			messageDiv.addClassName(style.statusError());
		} 
		messageDiv.setInnerText(message.getMessage());
	}
}
