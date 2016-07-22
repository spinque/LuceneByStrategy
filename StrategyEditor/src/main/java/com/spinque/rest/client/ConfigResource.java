package com.spinque.rest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ConfigResource extends Constants {
	
	public static ConfigResource INSTANCE = GWT.create(ConfigResource.class);

	@Key("REST_URL")
	public String getRestURL();

	@Key("VERSION")
	@DefaultStringValue("unknown")
	public String getVersion();

	@Key("DEBUG")
	@DefaultBooleanValue(false)
	public boolean getDebugMode();
	
	@Key("EDUCATION")
	@DefaultBooleanValue(false)
	public boolean getEducationMode();
	
	@Key("DEMO")
	@DefaultBooleanValue(false)
	public boolean getDemoMode();
	
	@Key("EDITOR_URL")
	public String getEditorURL();

	
	/* only used for query applications, not for the editor */
	@Key("WORKSPACE")
	public String getWorkspaceName();

	/* only used for query applications, not for the editor */
	@Key("CONFIGURATION")
	public String getConfigurationName();

	@Key("TITLE")
	public String getTitle();

	@Key("STRATEGY_EDITABLE")
	public boolean isStrategyEditable();

	@Key("PAGE_SIZE")
	public int getPageSize();

	@Key("FACETS_SORTING")
	@DefaultStringValue("TITLE")
	public String getFacetsSorting();
}
