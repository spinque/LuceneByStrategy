package com.spinque.open.strategy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.spinque.open.blocks.LuceneBlock;

public class LuceneStrategy {

	private final LuceneBlock _resultBlock;
	private final String _strategyName;

	public LuceneStrategy(String strategyName, LuceneBlock luceneBlock) {
		_strategyName = strategyName;
		_resultBlock = luceneBlock;
	}
	
	public String getName() {
		return _strategyName;
	}

	public static LuceneStrategy load(File strategyFile, Map<String, String> query) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d =  db.parse(strategyFile);

		Element strategyElem = d.getDocumentElement();
		String strategyName = strategyElem.getAttribute("name");
		String resultBlockName = strategyElem.getAttribute("result");

		Map<String, LuceneBlock> blocks = new HashMap<String, LuceneBlock>();
		NodeList blockElems = strategyElem.getElementsByTagName("block");
		for (int i = 0; i< blockElems.getLength(); i++) {
			Element blockElem = (Element) blockElems.item(i);
			LuceneBlock b = LuceneBlockFactory.parse(blockElem, blocks, query);
			blocks.put(b.getName(), b);
		}
		
		LuceneBlock resultBlock = blocks.get(resultBlockName);
		if (resultBlock == null)
			throw new IOException("no result block " + resultBlockName);
		return new LuceneStrategy(strategyName, resultBlock);
	}

	public void close() throws IOException {
		_resultBlock.close();
	}

	public LuceneBlock getResultBlock() {
		return _resultBlock;
	}
}
