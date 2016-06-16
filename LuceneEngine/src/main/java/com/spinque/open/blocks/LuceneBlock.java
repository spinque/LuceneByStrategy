package com.spinque.open.blocks;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.spinque.open.LuceneBlockResultStore;
import com.spinque.open.LuceneExecutor;


public abstract class LuceneBlock {
	
	protected static final int MAX_RESULTS = 1000000;
	
	public enum ResultAggregationType {
		
		LAST, MAX, SUM, AVG, DISTINCT;

		public static ResultAggregationType parse(String value, ResultAggregationType defaultValue) {
			if (value == null)
				return defaultValue;
			try {
				return ResultAggregationType.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to parse " + value + " (using default " + defaultValue + ")");
				return defaultValue;
			}
		} 
	};

	protected final Query _getAll_Query = new MatchAllDocsQuery();
	private Map<String,List<Double>> _resultAggregation = null;
	private List<LuceneBlock> _inputBlocks;
	private String _name = null;
	private ResultAggregationType _resultAggregationType = ResultAggregationType.LAST;
	protected LuceneBlockResultStore _resultStore;
	
	public LuceneBlock() {
		_resultStore = new LuceneBlockResultStore();
	}

	protected abstract void checkValid() throws Exception;
	
	protected abstract void computeAndStoreResults() throws Exception; 

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public ResultAggregationType getResultAggregationType() {
		return _resultAggregationType;
	}

	public void setResultAggregationType(ResultAggregationType resultAggregationType) {
		_resultAggregationType = resultAggregationType;
	}

	public List<LuceneBlock> getInputBlocks() {
		return _inputBlocks;
	}

	public void setInputBlocks(List<LuceneBlock> inputBlocks) {
		_inputBlocks = inputBlocks;
	}
	public void setInputBlocks(LuceneBlock inputBlock) {
		_inputBlocks = Collections.singletonList(inputBlock);
	}
	public void setInputBlocks(LuceneBlock[] inputBlockss) {
		_inputBlocks = Arrays.asList(inputBlockss);
	}
	
	public IndexSearcher getSearcher() throws Exception {
		checkValid();
		if (needsRecompute()) {
			_resultAggregation = new HashMap<String,List<Double>>();
			computeAndStoreResults();
		}
		return _resultStore.getSearcher();
	}
	
	public TopDocs getResults() throws Exception {

		/*
		 * Here we would like the search to use a custom sort, according to the documentation:
		 * 
				SortField sortfield = new SortField("prob", SortField.Type.DOUBLE, true);
		        TopDocs tds = s.search(_getAll_Query, 10, new Sort(sortfield)); 
		 * But it doesn't seem to work.
		 * Instead, we explicitly sort at result presentation.
		 */
        
		return getSearcher().search(_getAll_Query, MAX_RESULTS);
	}
	
	protected Document fetchFromChildren(String uid) throws Exception {
		Query query =  new TermQuery(new Term("_uid",uid));
		for (LuceneBlock b : getInputBlocks()) {
			IndexSearcher s = b.getSearcher();
			TopDocs tds = s.search(query, 1);
			if (tds.totalHits > 0)
				return s.doc(tds.scoreDocs[0].doc);
		}
		throw new IllegalStateException("uid '" + uid + "' not found");
	}


	protected boolean needsRecompute() {
		if (!_resultStore.getIsUpToDate())
			return true;
		for (LuceneBlock b : _inputBlocks) {
			if (b.needsRecompute())
				return true;
		}
		return false;
	}
	
	protected void addDocumentToResultAggregation(Document d) {
		List<Double> l = _resultAggregation.get(d.get("_uid"));
		if (l == null) {
			l = new ArrayList<Double>();
			_resultAggregation.put(d.get("_uid"), l);
		}
		l.add(d.getField("prob").numericValue().doubleValue());
	}
	
	protected void aggregateAndStoreResults() throws Exception {
		
		// Perform aggregation and store results
		for(Entry<String, List<Double>> e : _resultAggregation.entrySet()) {
			double prob=1d;
			switch (_resultAggregationType) {
				case LAST:
					break;
				case MAX:
					prob = 0d;
					for (double p : e.getValue()) {
						prob = Math.max(prob, p);
					}
					break;
				case SUM:
					prob = 0d;
					for (double p : e.getValue()) {
						prob = prob + p;
					}
					break;
				case AVG:
					prob = 0d;
					for (double p : e.getValue()) {
						prob = prob + p;
					}
					prob /= e.getValue().size();
					break;
				case DISTINCT:
					prob = 1d;
					for (double p : e.getValue()) {
						prob = prob * (1d-p);
					}
					prob = 1d-prob;
					break;
			default:
				throw new IllegalStateException("Aggregation type " + _resultAggregationType + " unknown");
			}
			Document d = fetchFromChildren(e.getKey());
			LuceneExecutor.setDocumentProb(d, prob);
			_resultStore.storeResult(d);
		}
		_resultAggregation.clear();
	}
	
	public void close() throws IOException {
		_resultStore.close();
	}
}
