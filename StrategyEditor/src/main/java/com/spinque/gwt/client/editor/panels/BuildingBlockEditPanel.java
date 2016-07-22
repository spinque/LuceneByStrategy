package com.spinque.gwt.client.editor.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.spinque.gwt.client.editor.ServerSession;
import com.spinque.gwt.client.editor.primitives.SVGBuildingBlock;
import com.spinque.gwt.utils.client.dialog.ChangedHandler;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel;
import com.spinque.gwt.utils.client.dialog.DialogContentsPanel.PopupContents;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.NotificationOverlay;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.ControlInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ControlOverlay;

/**
 * Should provide a window which makes it possible to:
 * 
 * - change the name of the block. (not necessary)
 * 
 * - enable/disable configurability of parameters
 * - edit each parameter (name, title, group(cluster), default value).
 * 
 * - save / cancel button
 *  
 */
public class BuildingBlockEditPanel extends Composite implements PopupContents {
	
	interface MyUiBinder extends UiBinder<Widget, BuildingBlockEditPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	 
	@UiField SpanElement nameSpan;
	@UiField VerticalPanel controlsPanel;
	@UiField DivElement noItems;
	
//	private final List<ParameterControlEditPanel> _controlPanels = new ArrayList<ParameterControlEditPanel>();
	private final SVGBuildingBlock _svgBuildingBlock;
	
	public BuildingBlockEditPanel(SVGBuildingBlock svgBuildingBlock) {
		initWidget(uiBinder.createAndBindUi(this));
		
		_svgBuildingBlock = svgBuildingBlock;
		nameSpan.setInnerText(_svgBuildingBlock.getInstance().getBuildingBlock());
		
		String blockName = svgBuildingBlock.getInstance().getName();
		if (_svgBuildingBlock.getInstance().getControls().length() == 0) {
			noItems.getStyle().setDisplay(Display.BLOCK);
		} else {
			boolean first = true;
			List<String> controlNames = getControlNames(_svgBuildingBlock.getBlock());
			Collections.sort(controlNames);
			for (String name : controlNames) {
				ControlInstanceOverlay cio = getControl(name);
				ControlOverlay co = getBlockControl(name);
				ParameterControlEditPanel pcep = new ParameterControlEditPanel(blockName, co, cio, this);
				controlsPanel.add(pcep);
				if (first) { pcep.setFocus(); first=false;	}
			}
		}
	}
	
	/** Find a control by its name */
	private ControlOverlay getBlockControl(String name) {
		for (int i = 0; i < _svgBuildingBlock.getBlock().getControls().length(); i++) {
			ControlOverlay co = _svgBuildingBlock.getBlock().getControls().get(i);
			if (co.getControl().equals(name))
				return co;
		}
		return null;
	}

	/** Find a control-instance by its name */
	private ControlInstanceOverlay getControl(String name) {
		for (int i = 0; i < _svgBuildingBlock.getInstance().getControls().length(); i++) {
			ControlInstanceOverlay cio = _svgBuildingBlock.getInstance().getControls().get(i);
			if (cio.getControl().equals(name))
				return cio;
		}
		return null;
	}

	/** enumerate the names of the controls in a block */
	private List<String> getControlNames(BuildingBlockOverlay block) {
		List<String> result = new ArrayList<String>(); 
		for (int i = 0; i < block.getControls().length(); i++) {
			ControlOverlay co = block.getControls().get(i);
			result.add(co.getControl());
		}
		return result;
	}

	private final Set<ParameterControlEditPanel> changedParameters = new HashSet<ParameterControlEditPanel>();

	private ChangedHandler _handler = null;
	
	public void markChanged(ParameterControlEditPanel param, boolean changed) {
		if (changed) {
			changedParameters.add(param);
		} else {
			changedParameters.remove(param);
		}
		if (_handler != null)
			_handler.changedEvent(!changedParameters.isEmpty());
	}
	
	
	@Override
	public Widget getContent() {
		return this;
	}

	@Override
	public void applyChanges(final DialogContentsPanel pp) {
		/* control -> value */
		Map<String, String> values = new HashMap<String, String>();
		for (ParameterControlEditPanel pcep : changedParameters) {
			values.put(pcep.getName(), pcep.getValue());
		}
		if (values.size() > 0) {
			ServerSession.get().setParams(_svgBuildingBlock.getInstance().getName(), 
				values,
				new ResponseTask<BuildingBlockInstanceOverlay>() {

					@Override
					public void run(BuildingBlockInstanceOverlay bbio) {
						// do nothing
						// FIXME: update the state of the strategy...
//						_svgBuildingBlock.getInstance().setControls(bbio.getControls());
						pp.close();
						_svgBuildingBlock._instance = bbio;
						_svgBuildingBlock.update();
						ServerSession.get().notifyChange();
					}

					@Override
					public void failed(Throwable exception, String debugDescription) {
						Window.alert("Failed to notify changes");
					}
			}, "change parameters");
		} else {
			// no changes... do nothing
		}
	}

	public static BuildingBlockEditPanel generate(
			SVGBuildingBlock svgBuildingBlock) {
		return new BuildingBlockEditPanel(svgBuildingBlock);
	}

	@Override
	public HandlerRegistration addChangedHandler(ChangedHandler handler) {
		_handler = handler;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				_handler = null;
			}
		};
	}
}
