package com.spinque.open.strategy;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import com.spinque.open.blocks.LuceneBlock;
import com.spinque.open.blocks.LuceneBlock.ResultAggregationType;
import com.spinque.open.blocks.LuceneGeoSearchBlock;
import com.spinque.open.blocks.LuceneMixBlock;
import com.spinque.open.blocks.LuceneSearchBlock;
import com.spinque.open.blocks.LuceneSourceBlock;
import com.spinque.open.blocks.LuceneTraverseBlock;
import com.spinque.open.blocks.LuceneUtils;

public class LuceneBlockFactory {

	public static LuceneBlock parse(Element blockElem, Map<String, LuceneBlock> blocks, Map<String, String> query) throws IOException {
		String name = blockElem.getAttribute("name");
		String type = blockElem.getAttribute("type");
		
		try {
			LuceneBlock result = null;
			if (type.equals("source")) {
				result = LuceneSourceBlock.makeBlock(blockElem, query);
			} else if (type.equals("search")) {
				result = LuceneSearchBlock.makeBlock(blockElem, query);
			} else if (type.equals("traverse")) {
				result = LuceneTraverseBlock.makeBlock(blockElem, query);
			} else if (type.equals("mix")) {
				result = LuceneMixBlock.makeBlock(blockElem, query);
			} else if (type.equals("geo")) {
				result = LuceneGeoSearchBlock.makeBlock(blockElem, query);
			}
			if (result != null) {
				result.setName(name);
				
				List<String> inputs = LuceneUtils.getTextContentOfElements(blockElem, "input");
				List<LuceneBlock> inputBlocks = LuceneUtils.getAll(inputs, blocks);
				result.setInputBlocks(inputBlocks);
				
				String resultAggregationType = LuceneUtils.getTextContentOfElement(blockElem, "resultAggregationType", false);
				if (resultAggregationType != null)
					result.setResultAggregationType(
						ResultAggregationType.parse(resultAggregationType, ResultAggregationType.MAX));
			}
			return result;
		} catch (IOException e) {
			throw new IOException("failed to parse block " + name, e);
		}
	}

	public static String replaceQueryParameters(String value, Map<String, String> query) {
		String result = value;
		for (Entry<String, String> entry : query.entrySet()) {
			if (result.indexOf('%') == -1)
				break;
			result = result.replace('%' + entry.getKey() + '%', entry.getValue());
		}
		return result;
	}
}
