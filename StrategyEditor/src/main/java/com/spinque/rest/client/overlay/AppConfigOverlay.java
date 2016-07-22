package com.spinque.rest.client.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class AppConfigOverlay extends JavaScriptObject {

	protected AppConfigOverlay() { }

	public final native JavaScriptObject getRenderingConfig() /*-{
		return this.rendering;
	}-*/;
	
	public final native JavaScriptObject getBrowsingConfig() /*-{
		return this.browsing;
	}-*/;
	
	public final native String getTitle() /*-{
		return this.title;
	}-*/;
	
	public final native String getDecosDocumentBase() /*-{
		return this.decos_document_base;
	}-*/;

	public final native String getDashboardFolder() /*-{
		return this.dashboard;
	}-*/;

	public final native BrowserConfigOverlay getBrowserConfig() /*-{
		return this.browser;
	}-*/;
	
	public final native MarkedItemsConfigOverlay getMarkedItems() /*-{
		return this.markeditems;
	}-*/;

	public static class MarkedItemsConfigOverlay extends JavaScriptObject {
		
		protected MarkedItemsConfigOverlay() { }

		public final native String getSelectFacet() /*-{
			return this.selectfacet;
		}-*/;
	}
	
	public static class BrowserStrategyOverlay extends JavaScriptObject {
		
		protected BrowserStrategyOverlay() { }
		
		public static final native BrowserStrategyOverlay create(String strategy, String title) /*-{
			return { "strategy": strategy, "title": title };
		}-*/;
		
		public final native String getStrategy() /*-{
			return this.strategy;
		}-*/;
		
		public final String getTitle() {
			if (getTitleStr() == null) {
			   /* if no title is provided, 
			    * try to beautify the (ugly) internal strategy name, see EQUIP-102 */
			   String title = getStrategy();
			   title = title.startsWith("related/") ? title.substring(8) : title;
			   title = title.replace('_', ' ');
			   return title;
			} else {
				return getTitleStr();
			}
		}
		
		private final native String getTitleStr() /*-{
			return this.title;
		}-*/;
	}
	
	public static class BrowserConfigOverlay extends JavaScriptObject {
		
		protected BrowserConfigOverlay() { }
		
		public final native String getIDStrategy() /*-{
			return this.idstrategy;
		}-*/;

		public final native JsArray<BrowserStrategyOverlay> getDefaultSet() /*-{
			for (index = 0; index < this.defaultSet.length; ++index) {
			   if (!(this.defaultSet[index] instanceof Object)) {
			      var s = this.defaultSet[index];
				  this.defaultSet[index] = { "strategy": s, "title": null };
			   }
			}
			return this.defaultSet;
		}-*/;

		public final native JsArrayString getClassNames() /*-{
			var l = [];
			for (var key in this.specific) {
		  		l.push(key);
			}
			return l; 
		}-*/;
		
		public final native JsArray<BrowserStrategyOverlay> getSetForClass(String name) /*-{
		    for (index = 0; index < this.specific[name].length; ++index) {
			   if (!(this.specific[name][index] instanceof Object)) {
			      var s = this.specific[name][index];
				  this.specific[name][index] = { "strategy": s, "title": null };
			   }
			}
			return this.specific[name];
		}-*/;
	}

}
