package com.spinque.gwt.utils.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Various JSON-manipulating utils.
 */
public class UtilsJSON {

	/**
	 * Concatenates two arrays into a new array.
	 * Probably not the most efficient implementation.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public final static <C extends JavaScriptObject> JsArray<C> concat(
			JsArray<C> a, JsArray<C> b) {
		if (a == null || a.length() == 0)
			return b;
		if (b == null || b.length() == 0)
			return a;
		
		JsArray<C> result =  JavaScriptObject.createArray().<JsArray<C>>cast();
		for (int i = 0; i< a.length(); i++) 
			result.push(a.get(i));
		for (int i = 0; i< b.length(); i++) 
			result.push(b.get(i));
		return result;
	}

	public final static <C extends JavaScriptObject> void sort(JsArray<C> data,
			Comparator<? super C> comparator) {
		// bubble-sort
		for (int i = 0; i < data.length() - 1; i++) {
			for (int j = 0; j < data.length() - 1; j++){
				C a = data.get(j);
				C b = data.get(j+1);
				if (comparator.compare(a, b) > 0) {
					data.set(j, b);
					data.set(j+1, a);
				}
			}
		}
	}

	/**
	 * Creates a JSON-string from any JavaScript object.
	 * 
	 * @param obj
	 * @return
	 */
	public final static native <C extends JavaScriptObject> String toJSON(C obj) /*-{
		return JSON.stringify(obj);
	}-*/;

	/**
	 * Takes in a trusted JSON String and evals it.
	 * @param JSON String that you trust
	 * @return JavaScriptObject that you can cast to an Overlay Type
	 * 
	 * 
	 */
	/*  */
//	if (jsonStr == null || jsonStr.length == 0);   
//	return null;
	public static native JavaScriptObject parseJson(String jsonStr) /*-{
	  	return eval("(" + jsonStr + ")");
	}-*/;
	
	
	/** 
	 * Somehow sorting strings Arrays.sort() does not always work
	 * when the data comes from JSON objects (get a class cast failure).
	 * If the objects are know to be strings, this function can be used.
	 */
	public static final Comparator<? super String> STRING_COMPARATOR = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.charAt(0) != o2.charAt(0))
					return o1.charAt(0) - o2.charAt(0);
				return o1.compareTo(o2);
			}
	};

	public interface Grouper<T> {
		String getGroupName(T data);
	}
	
	public static <T extends JavaScriptObject> Map<String, List<T>> groupBy(
			JsArray<T> data, Grouper<T> grouper) {
		Map<String, List<T>> params = new HashMap<String, List<T>>();
		for (int i = 0; i < data.length(); i++) {
			T spo = data.get(i);

			if (spo == null)
				continue;
			String groupName = grouper.getGroupName(spo);
			if (!params.containsKey(groupName))
				params.put(groupName, new ArrayList<T>());
			params.get(groupName).add(spo);
		}
		return params;
	}

	public static <T extends JavaScriptObject> List<T> orderBy(
			JsArray<T> options,
			Comparator<T> comparator) {
		List<T> result = new ArrayList<T>(options.length());
		for (int i = 0; i < options.length(); i++)
			result.add(options.get(i));
		Collections.sort(result,comparator);
		return result;
	}
}
