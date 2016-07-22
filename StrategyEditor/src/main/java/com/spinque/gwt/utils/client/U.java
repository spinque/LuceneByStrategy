package com.spinque.gwt.utils.client;

import java.util.Date;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormat;

/**
 * Utils for constructing/filling GWT's UI-objects.
 */
public class U {
	
	/**
	 * Creates a time-representation in hours, minutes and seconds given
	 * a time in milliseconds since midnight.
	 * (note: a value larger than 24 hours is not supported)
	 *
	 * @param value milliseconds since 00:00:00.000
	 * @return HH:mm:ss
	 */
	private static String millisToTime(int value) {
		DateTimeFormat dt = DateTimeFormat.getFormat("HH:mm:ss");
		return dt.format(new Date(value), TimeZone.createTimeZone(0));
	}

	/**
	 * Formats a number as a percentage (no decimals).
	 * 
	 * @param n the value to be formatted (1.0 = 100%)
	 * @return number expressed as a percentage including the '%' sign.
	 */
	public static String percFormat(double n) {
		return Math.round(n*100) + "%"; //  + "." + Math.round((n*10000) % 100) + "%";
	}
	
	/**
	 * Formats a value to an amount in euros.
	 * It prepends a euro-sign.
	 * It puts group-separators each 3 digits.
	 * 
	 * @param value The amount in whole euros.
	 * @return
	 */
	private static String formatEuro(Number value) {
		long longValue = (value.longValue() < 0) ? -value.longValue() : value.longValue();
		NumberFormat nf = NumberFormat.getCurrencyFormat("EUR");
		
		// Some how should set "nl_NL" as the locale might still make a difference:
		// See: http://stackoverflow.com/questions/9777689/how-to-get-numberformat-instance-from-currency-code
		String str = nf.format(longValue);
//		if (str.endsWith(",000.00"))
		if (str.length() > 7)
			str = str.substring(0, str.length() - 7) + "k";
//		if (str.endsWith(".00"))
//			str = str.substring(0, str.length() - 3);
		if (value.longValue() < 0)
			str = "<font color=\"red\">- " + str + "</font>";
		return str;
//		if (Math.abs(value) < 1000) {
//			return "&#8364; " + value + ",-";
//		} else if (Math.abs(value) < 1000000) {
//			return "&#8364; " + (value/1000) + "." + (Math.abs(value)%1000) + ",-";	
//		} else {
//			return "&#8364; " + ((value/1000000) % 1000) + "." + ((Math.abs(value)/1000) % 1000) + "." + (Math.abs(value)%1000) + ",-";	
//		} 
	}
}
