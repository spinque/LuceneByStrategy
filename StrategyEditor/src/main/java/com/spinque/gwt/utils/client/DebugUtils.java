package com.spinque.gwt.utils.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;

public class DebugUtils {

	
	/**
	 * Sets an uncaught exception handler, and afterwards loads
	 * the application.
	 * 
	 * @param sep
	 */
	public static void wrapDebug(final SpinqueEntryPoint sep) {
		 GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
		      public void onUncaughtException(Throwable throwable) {
		        String text = "Uncaught exception: ";
		        while (throwable != null) {
		          StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		          text += throwable.toString() + "\n";
		          for (int i = 0; i < stackTraceElements.length; i++) {
		            text += "    at " + stackTraceElements[i] + "\n";
		          }
		          throwable = throwable.getCause();
		          if (throwable != null) {
		            text += "Caused by: ";
		          }
		        }
		        DialogBox dialogBox = new DialogBox(true, false);
		        DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
		        System.err.print(text);
		        text = text.replaceAll(" ", "&nbsp;");
		        dialogBox.setHTML("<pre>" + text + "</pre>");
		        dialogBox.center();
		      }
		    });
		 // use a deferred command so that the handler catches onModuleLoad2() exceptions
		 Scheduler.get().scheduleDeferred(new Command() {
			 public void execute() {
				 sep.load();
			 }
		 });
	}
}
