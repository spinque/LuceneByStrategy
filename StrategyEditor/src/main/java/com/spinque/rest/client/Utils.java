package com.spinque.rest.client;

import java.util.Comparator;
import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

public class Utils {

	public static void clearPanel(WidgetCollection wc) {
		Iterator<Widget> iw = wc.iterator();
		while (iw.hasNext()) {
			iw.next();
			iw.remove();
		}
	}

	public interface SimpleResponseTask<T> {
		void run(T json);
		void failed(Throwable exception, String debugDescription);
	}
	
	public interface ResponseTask<T extends JavaScriptObject> extends SimpleResponseTask<T> {
		
	}
	
	public static abstract class DefaultResponseTask<T extends JavaScriptObject> implements ResponseTask<T> {

		@Override
		public void failed(Throwable exception, String debugDescription) {
		}
	}

	
	/**
	 * Simple BubbleSort implementation.
	 * 
	 * @param data
	 * @param comparator
	 */
	public static <C extends JavaScriptObject> void sortArray(JsArray<C> data,
			Comparator<C> comparator) {
		for (int i = 0; i < data.length() - 1; i++) {
			for (int j = 0; j < data.length() - 1; j++) {
				int result = comparator.compare(data.get(j), data.get(j+1));
				if (result > 0)
					swap(data, j, j+1);
			}
		}
	}
	
	/**
	 * Swaps 2 elements in an JsArray.
	 * @param data
	 * @param a
	 * @param b
	 */
	public static <C extends JavaScriptObject> void swap(JsArray<C> data, int a, int b) {
		C x = data.get(a);
		data.set(a, data.get(b));
		data.set(b, x);
	}
}
