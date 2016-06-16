package com.spinque.open.blocks;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.spinque.open.LuceneBlockResultStore;
import com.spinque.open.strategy.LuceneBlockFactory;

public class LuceneSourceBlock extends LuceneBlock {

	public LuceneSourceBlock(Directory d) throws IOException  {
		_resultStore = new LuceneBlockResultStore(d);
	}

	
	@Override
	protected boolean needsRecompute() {
		/* results are static, never need to recompute */
		return false;
	}

	@Override
	protected void checkValid() throws Exception {
	}

	@Override
	protected void computeAndStoreResults() throws Exception {
		/* results are static, nothing to do here */
	}

	public static LuceneBlock makeBlock(Element blockElem, Map<String, String> query) throws IOException {
		NodeList nlSource = blockElem.getElementsByTagName("source");
		if (nlSource.getLength() != 1)
			throw new IOException("expected 1 source element");
		String sourceDef = nlSource.item(0).getTextContent();
		String source = LuceneBlockFactory.replaceQueryParameters(sourceDef, query);
		Path indexStore = Paths.get(source);
		Directory idxDir = new SimpleFSDirectory(indexStore);
		return new LuceneSourceBlock(idxDir);
	}
}
