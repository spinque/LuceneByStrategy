package com.spinque.open.blocks;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;

import com.spinque.open.LuceneExecutor;


public class LuceneTraverseBlock extends LuceneBlock {

	private String _fromField = null;
	private String _toField;
	private Boolean _reverseRelation;

	public LuceneTraverseBlock() {
		setToField("_uid");
		setReverseRelation(false);
		setResultAggregationType(ResultAggregationType.MAX);
	}
	
	public Boolean getReverseRelation() {
		return _reverseRelation;
	}

	public void setReverseRelation(Boolean reverseRelation) {
		_reverseRelation = reverseRelation;
	}

	public String getFromField() {
		return _fromField;
	}

	public void setFromField(String fromField) {
		if (!fromField.equals(_fromField)) {
			_fromField = fromField;
			_resultStore.setIsUpToDate(false);
		}
	}

	public String getToField() {
		return _toField;
	}

	public void setToField(String toField) {
		if (!toField.equals(_toField)) {
			_toField = toField;
			_resultStore.setIsUpToDate(false);
		}
	}

	@Override
	public void computeAndStoreResults() throws Exception {
		
		IndexSearcher fromSearcher = getFromBlock().getSearcher();
    	IndexSearcher toSearcher = getToBlock().getSearcher();
		
		TopDocs fromResults = getFromBlock().getResults();
		
		// Nested-loop Join
		for (ScoreDoc fsd : fromResults.scoreDocs) {
		    Document fd = fromSearcher.doc(fsd.doc);
		    double fromProb = LuceneExecutor.getProbFromDocument(fd);
		    String to_list[] = fd.getValues(_reverseRelation ? "rev_"+_fromField : _fromField);
		    
		    for (String to : to_list) {
		    	Query query = new TermQuery(new Term(_toField, to)); 
		    	TopDocs toResults = toSearcher.search(query, MAX_RESULTS);
		    	for (ScoreDoc tsd : toResults.scoreDocs) {
				    Document td = toSearcher.doc(tsd.doc);
				    double toProb = LuceneExecutor.getProbFromDocument(td);
				    double prob = toProb * fromProb;
				    LuceneExecutor.setDocumentProb(td, prob);
				    addDocumentToResultAggregation(td);
				}
		    }
		}
		aggregateAndStoreResults();
		_resultStore.commitAndFinalizeResultStore();
	}

	private LuceneBlock getFromBlock() {
		checkValid();
		return getInputBlocks().get(0);
	}

	private LuceneBlock getToBlock() {
		checkValid();
		return getInputBlocks().get(1);
	}
	
	@Override
	protected void checkValid() {
		List<LuceneBlock> inputBlocks = getInputBlocks(); 
		
		if (inputBlocks == null || inputBlocks.size() != 2)
			throw new IllegalStateException("Two blocks (From,To) need to be connected");
		
		if (_fromField == null || _toField == null || _fromField.isEmpty() || _toField.isEmpty())
			throw new IllegalStateException("fromField and toField parameters need to be set");
	}
	
	public static LuceneBlock makeBlock(Element blockElem, Map<String, String> query) throws IOException {
		LuceneTraverseBlock result = new LuceneTraverseBlock();
		
		String fromField = LuceneUtils.getTextContentOfElement(blockElem, "fromField", true);
		String toField = LuceneUtils.getTextContentOfElement(blockElem, "toField", false);
		String reverseRelation = LuceneUtils.getTextContentOfElement(blockElem, "reverse", false);
		
		result.setFromField(fromField);
		if (toField != null) result.setToField(toField);
		try {
			result.setReverseRelation(Boolean.parseBoolean(reverseRelation));
		} catch (Exception e) {
			// keep default
		}
		return result;
	}
}
