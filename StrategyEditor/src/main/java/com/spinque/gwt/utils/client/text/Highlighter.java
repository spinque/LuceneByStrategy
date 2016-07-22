package com.spinque.gwt.utils.client.text;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.spinque.gwt.utils.client.Utils;
import com.spinque.gwt.utils.client.text.HighlightStyle.HighlightCss;

public class Highlighter {
	
	
	public static String makeSummary(String data, String[] keywords, int expectedLength) {
		return makeSummary(data, keywords, expectedLength, getDefaultStyles());
	}
	
	public static String makeSummary(String data, String[] keywords, int expectedLength, String[] styleNames) {
		if (data == null)
			return null;
			
		if (keywords == null || keywords.length == 0) {
			return (data.length() <= expectedLength) ? data : (data.substring(0,expectedLength) + "...");
		}
		
		try {
			RegExp keywordMatcher = RegExp.compile("(\\b" + Utils.joinStrings(keywords, "\\b)|(\\b") + "\\b)", "i");
			return makeSummary(data, keywordMatcher, keywords, expectedLength, styleNames);
		} catch (RuntimeException e) {
			return "[FAILED TO HIGHLIGHT] " + data;
		}
	}
	
	public static String makeSummary(String data, RegExp keywordMatcher, String[] keywords, int expectedLength, String[] styleNames) {
		int n = countOccurrences(data, keywordMatcher);
		switch (n) {
		case 0:
			return (data.length() <= expectedLength) ? data : (data.substring(0,expectedLength) + "...");
		case 1: {
			int pos = getPosition(data, keywordMatcher, 0);
			int start = (pos > expectedLength / 2) ? pos - expectedLength / 2 : 0;
			int end = (pos < data.length() - expectedLength / 2) ? pos + expectedLength / 2 : data.length();
			return 
				((start > 0) ? "..." :"") 
				+ highlight(data.substring(start, end), keywordMatcher, styleNames) 
				+ ((end < data.length()) ? "..." :"");
		}
		default: {
			// FIXME: implement
//			return highlight(data, keywordMatcher);
			int pos = getPosition(data, keywordMatcher, 0);
			int start = (pos > expectedLength / 2) ? pos - expectedLength / 2 : 0;
			int end = (pos < data.length() - expectedLength / 2) ? pos + expectedLength / 2 : data.length();
			return 
				((start > 0) ? "..." :"") 
				+ highlight(data.substring(start, end), keywordMatcher, styleNames) 
				+ ((end < data.length()) ? "..." :"");
		}
		}
	}
	
	private static int countOccurrences(String data, RegExp keywordPattern) {
		int i = 0;
		int pos = 0;
		while (true) {
			MatchResult m = keywordPattern.exec(data.substring(pos));
			if (m == null)
				break;
			pos += m.getIndex() + Math.max(m.getGroup(0).length(), 1);
			i++;
		}
		return i;
	}

	private static int getPosition(String data, RegExp keywordPattern, int n) {
		int i = 0;
		int pos = 0;
		while (true) {
			MatchResult m = keywordPattern.exec(data.substring(pos));
			if (m == null)
				break;
			if (i == n)
				return pos + m.getIndex();
			pos += m.getIndex() + Math.max(m.getGroup(0).length(), 1);
			i++;
		}
		return -1;
	}

	private static String highlight(String data, RegExp keywordPattern, String[] styleNames) {
		StringBuffer sb = new StringBuffer();
		int pos = 0;
		while (true) {
			MatchResult m = keywordPattern.exec(data.substring(pos));
			if (m == null)
				break;
			sb.append(data.substring(pos, pos + m.getIndex()));
			for (int i = 0; i < m.getGroupCount(); i++) {
				if (m.getGroup(i+1) != null) {
					sb.append("<span class=\"" + styleNames[i % styleNames.length] + "\">" + m.getGroup(i+1) + "</span>");
				}
			}
			pos += m.getIndex() + Math.max(m.getGroup(0).length(), 1);
		}
		sb.append(data.substring(pos));
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(makeSummary("de aap en de beer maken vuur", new String[] { "beer", "aap" }, 30, new String[] { "A", "B" }));
	}

	public static String[] getDefaultStyles() {
		HighlightCss style = HighlightStyle.INSTANCE.style();
		return new String[] { 
				style.highlight1(), style.highlight2(), 
				style.highlight3(), style.highlight4(), style.highlight5() 
		};
	}
}
