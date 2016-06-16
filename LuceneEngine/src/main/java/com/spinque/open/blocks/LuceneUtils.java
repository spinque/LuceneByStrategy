package com.spinque.open.blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LuceneUtils {

	public static <C,D> List<D> getAll(Collection<C> candidates, Map<C, D> mapping) {
		List<D> result = new ArrayList<D>();
		for (C candidate : candidates)
			result.add(mapping.get(candidate));
		return result;
	}

	public static List<String> getTextContentOfElements(Element elem, String tagName) {
		List<String> result = new ArrayList<String>();
		NodeList inputElems = elem.getElementsByTagName(tagName);
		for (int i = 0; i < inputElems.getLength(); i++) {
			Element childElem = (Element) inputElems.item(i);
			result.add(childElem.getTextContent());
		}
		return result;
	}

	public static String getTextContentOfElement(Element elem, String tagName, boolean required) throws IOException {
		List<String> result = getTextContentOfElements(elem, tagName);
		if (result.isEmpty() && !required)
			return null;
		if (result.size() != 1)
			throw new IOException("expected exactly one " + tagName + " element");
		return result.get(0);
	}
	
	/**
	 * https://issues.apache.org/jira/browse/LUCENE-7171 : 
	 * StringField like "_uid" need to be re-created to set tokenized=false,
	 * which is lost when reading from previous index (thus becoming TextField).
	 * 
	 * @param d
	 * @return d
	 */
	public static Document fixLUCENE7171(Document d) {
		List<String> fieldNames = new ArrayList<String>();
		for (IndexableField f : d.getFields()) {
			if (f.name().startsWith("_")) {
				fieldNames.add(f.name());
			}
		}
		for (String fieldName : fieldNames) {
			String value = d.getField(fieldName).stringValue();
			d.removeField(fieldName);
			d.add(new StringField(fieldName, value, Field.Store.YES));
		}
		return d;
	}
}
