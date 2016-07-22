package com.spinque.gwt.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.spinque.gwt.client.editor.library.BlockLoader;
import com.spinque.gwt.client.editor.primitives.SVGConnectionLine;
import com.spinque.gwt.client.editor.primitives.StrategyCanvas;
import com.spinque.gwt.utils.client.Utils;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.MessageOverlay;
import com.spinque.rest.client.overlay.NotificationOverlay;
import com.spinque.rest.client.overlay.StatusOverlay;
import com.spinque.rest.client.overlay.WorkspaceDetailsOverlay;
import com.spinque.rest.client.overlay.BuildingBlockInstanceOverlay.ControlInstanceOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ConnectionPointOverlay;

public class ServerSession {
	
	private static ServerSession INSTANCE; /* singleton */
	
	private String _strategyName;
	
	private final String _sessionID;
	private final String _projectRoot;
	private WorkspaceDetailsOverlay _wdo = null;
	private int uniqueID = 0;
	
	/* known blocks are blocks that:
	 * - are used in the strategy, or
	 * - have been viewed in the library-pane.
	 */
	Map<String, BuildingBlockOverlay> knownBlocks = new HashMap<String, BuildingBlockOverlay>();
	public void addToKnownBlocks(BuildingBlockOverlay block) {
		knownBlocks.put(block.getName(), block);
	}
	public BuildingBlockOverlay getKnownBlock(String blockName) {
		return knownBlocks.get(blockName);
	}
	
	private ServerSession(String sessionID, String projectRoot) {
		if (sessionID == null || projectRoot == null)
			throw new NullPointerException();
		
		_sessionID = sessionID;
		_projectRoot = projectRoot;
	}
	
	public static synchronized void initialize(String sessionID, 
			String projectRoot, final ResponseTask<WorkspaceDetailsOverlay> callback) {
		ServerSession ss = new ServerSession(sessionID, projectRoot);
		INSTANCE = ss;
		callback.run(null);
//		rh.doProjectRequest("/info", null, new ResponseTask<WorkspaceDetailsOverlay>() {
//
//			@Override
//			public void run(WorkspaceDetailsOverlay json) {
//				INSTANCE._wdo = json;
//				if (callback != null)
//					callback.run(json);
//			}
//
//			@Override
//			public void failed(Throwable exception, String debugDescription) {
//				INSTANCE._wdo = null;
//				if (callback != null)
//					callback.failed(exception, debugDescription);
//			}
//		}, "fetch workspace info");
	}

	public static boolean isInitialized() {
		return (INSTANCE != null && INSTANCE._wdo != null);
	}

	public static synchronized ServerSession get() {
		if (INSTANCE == null)
			throw new IllegalStateException("no session defined");
		return INSTANCE;
	}
	
	public void makeRequest(String task, String parameters, String arguments,
			ResponseTask<? extends JavaScriptObject> responseTask, String debugDescription) {
		String url = "/" + task + parameters; 
		String attributes = "&u=" + (uniqueID++) + "" + arguments;
//		_rh.doSessionRequest(url, attributes, responseTask, debugDescription);
	}
	
	public String getSessionRoot() {
		return _projectRoot + "/edit/" + getSessionID();
	}

	public String getSessionID() {
		if (_sessionID == null)
			throw new IllegalStateException("No sessionID yet...");
		return _sessionID;
	}

	public String getProjectRoot() {
		return _projectRoot;
	}
	
	public interface StrategyEditListener {
		public void notifyUpdate();
	}
	
	private final List<StrategyEditListener> _listeners = new ArrayList<ServerSession.StrategyEditListener>();

	public void notifyChange() {
		synchronized (_listeners) {
			for (StrategyEditListener listener: _listeners) {
				listener.notifyUpdate();
			}
		}
	}
	
	public void addListener(StrategyEditListener listener) {
		synchronized (_listeners) {
			_listeners.add(listener);
		}
	}

	public WorkspaceDetailsOverlay getWorkspaceDetails() {
		return _wdo;
	}

	
	public void setStrategyName(String strategy) {
		_strategyName = strategy;
	}
	public String getStrategyName() {
		return _strategyName;
	}
	
	/* info on strategy */
	SortedMap<String, BuildingBlockInstanceOverlay> _strategyBlocks = new TreeMap<String, BuildingBlockInstanceOverlay>();
	String resultBlock = null;
	SortedMap<String, String> _connections = new TreeMap<String, String>();
	
	public void addBlock(BuildingBlockOverlay block, int x, int y,
			ResponseTask<BuildingBlockInstanceOverlay> responseTask, String string) {
		BuildingBlockInstanceOverlay result = BuildingBlockInstanceOverlay.create(block, x, y);
		_strategyBlocks.put(result.getName(), result);
		responseTask.run(result);
	}
	public void setResult(String bbiName, ResponseTask<NotificationOverlay> responseTask, String string) {
		resultBlock = bbiName;
		responseTask.run(NotificationOverlay.create());
	}
	public void disconnect(SVGConnectionLine line, StrategyCanvas canvas) {
		_connections.remove(line.getBBIName() + ":" + line.getDestPointName());
		canvas.removeLine(line);
	}
	public void makeConnection(String sourceBBI, String sourcePoint, String destBBI, String destPoint,
			ResponseTask<BuildingBlockInstanceOverlay> responseTask, String string) {
		_connections.put(destBBI + ":" + destPoint, sourceBBI);
		responseTask.run(null);
	}
	public void setParams(String name, Map<String, String> values,
			ResponseTask<BuildingBlockInstanceOverlay> responseTask, String string) {
		BuildingBlockInstanceOverlay bbio = _strategyBlocks.get(name);
		JsArray<ControlInstanceOverlay> cio = bbio.getControls();
		for (Entry<String, String> entry : values.entrySet()) {
			Utils.log("updating " + entry.getKey() + " to " + entry.getValue());
			for (int i = 0; i < cio.length(); i++) {
				if (cio.get(i).getControl().equals(entry.getKey())) {
					Utils.log("found it!");
					cio.get(i).setValue(entry.getValue());
					break;
				}
			}
		}
		responseTask.run(bbio);
	}
	public void doDelete(String name) {
		_strategyBlocks.remove(name);
		Iterator<Entry<String, String>> iter = _connections.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			if (entry.getKey().startsWith(name + ":")
				|| entry.getValue().startsWith(name + ":"))
				iter.remove();
		}
		
	}
	public void isStrategyCorrect(ResponseTask<StatusOverlay> responseTask, String string) {
		if (resultBlock != null) {
			
			for (BuildingBlockInstanceOverlay bbio : _strategyBlocks.values()) {
				BuildingBlockOverlay bbo = BlockLoader.INSTANCE.getBlock(bbio.getBuildingBlock());
				for (int i = 0; i < bbo.getNeeds().length(); i++) {
					/* check if connected */
					ConnectionPointOverlay cpo = bbo.getNeeds().get(i);
					if (!_connections.containsKey(bbio.getName() + ":" + cpo.getName())) {
						responseTask.run(StatusOverlay.create(StatusOverlay.ERROR, MessageOverlay.create("block not fully connected: " + bbio.getName(), "ERROR")));
						return;
					} 
				}
			}
			responseTask.run(StatusOverlay.create(StatusOverlay.OK));
		} else {
			responseTask.run(StatusOverlay.create(StatusOverlay.ERROR, MessageOverlay.create("no result set", "ERROR")));
		}
	}
	public void compile(ResponseTask<NotificationOverlay> responseTask, String string) {
		try {
		    Document strategyDom = XMLParser.createDocument();
		    Element strategyElem = strategyDom.createElement("strategy");
		    strategyDom.appendChild(strategyElem);
		    strategyElem.setAttribute("name", "noname");
		    strategyElem.setAttribute("result", resultBlock);
		    
		    for (BuildingBlockInstanceOverlay bbio : _strategyBlocks.values()) {
		    	Element blockElem = toXML(strategyDom, bbio);
		    	strategyElem.appendChild(strategyDom.createTextNode("\r\n  "));
		    	strategyElem.appendChild(blockElem);
		    }
		    strategyElem.appendChild(strategyDom.createTextNode("\r\n"));
		    		
		    NotificationOverlay no = NotificationOverlay.create(NotificationOverlay.OK, strategyDom.toString());
			responseTask.run(no);
		 } catch (DOMException e) {
			responseTask.failed(null, "failed to compile strategy");
		 }
		
		
	}
	private Element toXML(Document strategyDom, BuildingBlockInstanceOverlay bbio) {
		Element result = strategyDom.createElement("block");
		result.setAttribute("name", bbio.getName());
		result.setAttribute("type", bbio.getBuildingBlock());
		for (Entry<String, String> entry : _connections.entrySet()) {
			if (entry.getKey().startsWith(bbio.getName() + ":")) {
				Element inputElem = strategyDom.createElement("input");
				inputElem.appendChild(strategyDom.createTextNode(entry.getValue()));
				result.appendChild(strategyDom.createTextNode("\r\n    "));
				result.appendChild(inputElem);
			}
		}
		for (int i = 0; i < bbio.getControls().length(); i++) {
			ControlInstanceOverlay cio = bbio.getControls().get(i);
			if (cio.getValue() != null && !cio.getValue().isEmpty()) {
				Element controlElem = strategyDom.createElement(cio.getControl());
				controlElem.appendChild(strategyDom.createTextNode(cio.getValue()));
				result.appendChild(strategyDom.createTextNode("\r\n    "));
				result.appendChild(controlElem);
			}
		}
		result.appendChild(strategyDom.createTextNode("\r\n  "));
		return result;
	}
}
