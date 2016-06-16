package com.spinque.open.blocks;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.spinque.open.LuceneExecutor;
import com.spinque.open.strategy.LuceneBlockFactory;

public class LuceneSearchBlock extends LuceneBlock {

	private String _queryString = null;
	
	public String getQueryString() {
		return _queryString;
	}

	public void setQueryString(String queryString) {
		if (!queryString.equals(_queryString)) {
			_queryString = queryString;
			_resultStore.setIsUpToDate(false);
		}
	}
	
	protected void computeAndStoreResults() throws Exception {
		/* get searcher from Docs input */
		IndexSearcher docsSearcher = getDocsBlock().getSearcher();

		/* Parse the query */
		Query query = new QueryParser("_uid", new StandardAnalyzer()).parse(_queryString);
		
		/* Search */
		TopDocs tds = docsSearcher.search(query, MAX_RESULTS);
		
		/* create new scored documents and store them as a new searchable index */
		for (ScoreDoc sd : tds.scoreDocs) {
		    Document d = docsSearcher.doc(sd.doc);
		    double docProb = LuceneExecutor.getProbFromDocument(d);
		    double prob = docProb * ( sd.score / Math.max(tds.getMaxScore(), 1d)); // prior * new score
		    LuceneExecutor.setDocumentProb(d, prob);
		    
		    _resultStore.storeResult(d);
		}
		_resultStore.commitAndFinalizeResultStore();
	}
	

	private LuceneBlock getDocsBlock() {
		List <LuceneBlock> inputBlocks = getInputBlocks();
		if (inputBlocks == null || inputBlocks.size() < 1)
			return null;
		return inputBlocks.get(0);
	}

	@Override
	protected void checkValid() throws Exception {
		if (getDocsBlock() == null)
			throw new IllegalStateException("Input docs block not connected");
		if (_queryString == null)
			throw new IllegalStateException("Query not defined");
	}

	public static LuceneBlock makeBlock(Element blockElem, Map<String, String> query) throws IOException {
		NodeList nlSource = blockElem.getElementsByTagName("queryString");
		if (nlSource.getLength() != 1)
			throw new IOException("expected 1 queryString element");
		String queryStringDef = nlSource.item(0).getTextContent();
		String queryString = LuceneBlockFactory.replaceQueryParameters(queryStringDef, query);
		
		LuceneSearchBlock searchBlock = new LuceneSearchBlock();
		searchBlock.setQueryString(queryString);
		return searchBlock;
	}
}
