package com.spinque.gwt.utils.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

public class Utils {
	
	public static native void log(String text) /*-{
  		console.log(text);
	}-*/;
	
	// invert the array
	public static int[] invertList(int[] pos) {
		int[] result = new int[pos.length];
		for (int i = 0; i < pos.length;i++) {
			result[pos[i]] = i;
		}
		return result;
	}

	/**
	 * Checks whether the given array is a permutation of its index numbers.
	 * 
	 * @param pos
	 * @return true iff the array is a permutation of the numberst
	 *  0 to array.length -1, false otherwise.
	 */
	public static boolean checkDense(int[] pos) {
		int[] dum = Arrays_copyOf(pos, pos.length);
		Arrays.sort(dum);
		for (int i = 0; i < dum.length; i++) {
			if (dum[i] != i) {
				return false;
			}
		}
		return true;
	}

	/**
	 * GWT doesn't supply a Arrays.copyOf... therefore we implement it ourselves.
	 * 
	 * @param pos
	 * @param length
	 * @return
	 */
	private static int[] Arrays_copyOf(int[] pos, int length) {
		int[] result = new int[pos.length];
		for (int i = 0; i< pos.length; i++)
			result[i] = pos[i];
		return result;
	}

	/**
	 * Takes two string parts, and makes sure that there is one
	 * '/' in between. (no more, no less)
	 * 
	 * So if either the first or the last contains a slash, no slash
	 * is added. If none contains a slash, a slash is added in between,
	 * if both contain a slash, one slash is stripped.
	 * 
	 * @param appbase
	 * @param workspace
	 * @return
	 */
	public static String joinPaths(String a, String b) {
		if (a == null || b == null)
			throw new NullPointerException();
		String result = a.endsWith("/") ? a : a + "/";
		result += b.startsWith("/") ? b.substring(1) : b;
		return result;		
	}

	/**
	 * joins strings just like python's join:
	 * 
	 * String[] source = new String[] { "aap", "beer", "noot" };
	 * joinStrings(source, "-") equals "aap-beer-noot";
	 * joinStrings(source, "//") equals "aap//beer//noot";
	 * 
	 * 
	 * @param keywords
	 * @param separator
	 * @throws NullPointerException if keywords or separator is null;
	 * @return
	 */
	public static String joinStrings(String[] keywords, String separator) {
		return joinStrings(keywords, separator, keywords.length);
	}

	public static String joinStrings(String[] keywords, String separator, int numParts) {
		if (separator == null || keywords == null)
			throw new NullPointerException();
		if (keywords.length == 0) return "";

		StringBuilder sb = new StringBuilder();
		sb.append(keywords[0]);
		for (int i = 1; i < numParts; i++) {
			sb.append(separator);
			sb.append(keywords[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Joins all strings that are not null.
	 * @param values
	 * @param value
	 * @return
	 */
	public static String joinNonNullStrings(Set<String> values, String separator) {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			if (!first) { sb.append(separator);  } else first=false;
			sb.append(s);
		}
		return sb.toString();
	}

	public static String multiply(char c, int length) {
		char[] result = new char[length];
		Arrays.fill(result, c);
		return new String(result);
	}
	

	/**
	 * @return a UrlBuilder object which contains a copy of 
	 * the current Window.Location.
	 */
	public static UrlBuilder copyCurrentLocation() {
		UrlBuilder ub = Window.Location.createUrlBuilder(); 
		ub.setHost(Window.Location.getHost());
		ub.setPath(Window.Location.getPath());
		ub.setProtocol(Window.Location.getProtocol());
		try {
			ub.setPort(Integer.parseInt(Window.Location.getPort()));
		} catch (NumberFormatException e) {
			// skip
		}
		for (Entry<String, List<String>> entry : Window.Location.getParameterMap().entrySet()) {
			ub.setParameter(entry.getKey(), entry.getValue().toArray(new String[] { }));
		}
		return ub;
	}

	public static String makeMapString(Map<String, Integer> items,
			String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, Integer> item : items.entrySet()) {
			if (first) first=false;
			else sb.append(separator);
			sb.append(item.getKey()).append('=').append(item.getValue());
		}
		return sb.toString();
	}

	public static Map<String, Integer> parseMapString(String value, String separator) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (value == null)
			return result;
		
		for (String s : value.split(separator)) {
			int pos = s.lastIndexOf("=");
			if (pos != -1) {
				try {
					result.put(s.substring(0, pos), Integer.parseInt(s.substring(pos+1)));
				} catch (NumberFormatException e) {
					// do nothing...
				}
			}
		}
		return result;
	}

}
