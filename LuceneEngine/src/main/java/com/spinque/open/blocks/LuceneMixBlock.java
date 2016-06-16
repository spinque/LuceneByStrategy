package com.spinque.open.blocks;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;

import com.spinque.open.LuceneExecutor;


public class LuceneMixBlock extends LuceneBlock {
	
	public LuceneMixBlock() {
		setResultAggregationType(ResultAggregationType.SUM);
	}

	private List<Double> _coefficients = null;
	
	public List<Double> getCoefficients() {
		return _coefficients;
	}
	
	public void setCoefficients(List<Double> coefficients) {
		/* Warning: this does not check that the sum of coefficient is 1.0 */
		if (!coefficients.equals(_coefficients)) {
			_coefficients = coefficients;
			_resultStore.setIsUpToDate(false);
		}
	}

	/**
	 * Convenience method to spread coefficients uniformly among the inputs
	 */
	public void setCoefficientsUniformly() {
		List<LuceneBlock> inputBlocks = getInputBlocks();
		if (inputBlocks == null || inputBlocks.isEmpty())
			return;
		int l = inputBlocks.size();
		Double c = 1d / l;
		_coefficients = new ArrayList<Double>(l);
		for (int i=0; i<l; i++)
			_coefficients.add(c);
		_resultStore.setIsUpToDate(false);
	}

	@Override
	public void computeAndStoreResults() throws Exception {
		List <LuceneBlock> inputBlocks = getInputBlocks(); 

		for (int i = 0; i < inputBlocks.size(); i++) {
			LuceneBlock b = inputBlocks.get(i);
			IndexSearcher inputSearcher = b.getSearcher();
			TopDocs tds = b.getResults();
			for (ScoreDoc sd : tds.scoreDocs) {
				Document d = inputSearcher.doc(sd.doc);
				double docProb = LuceneExecutor.getProbFromDocument(d);
				double prob = docProb * _coefficients.get(i); // prior * coefficient
				LuceneExecutor.setDocumentProb(d, prob);
				addDocumentToResultAggregation(d);
			}
		}
		aggregateAndStoreResults();
		_resultStore.commitAndFinalizeResultStore();
	}
	
	@Override
	protected void checkValid() throws Exception {
		List <LuceneBlock> inputBlocks = getInputBlocks(); 
		
		if (inputBlocks == null || inputBlocks.size() < 2)
			throw new IllegalStateException("Less than 2 blocks connected");
		if (_coefficients == null || _coefficients.size() != inputBlocks.size())
			throw new IllegalStateException("The number of coefficients (" + _coefficients.size() + ") does not match the number of inputs (" + inputBlocks.size() + ")");		
	}
	
	/*
	 * 		LuceneMixBlock mixBlock1 = new LuceneMixBlock();
		l = new LuceneBlock[] {searchBlock1, searchBlock2}; 
		mixBlock1.setInputBlocks(l);
		mixBlock1.setCoefficientsUniformly();
		mixBlock1.setResultAggregationType(ResultAggregationType.SUM);
		mixBlock1.setName("Mix Search 1 and Search 2");

	 */
	public static LuceneBlock makeBlock(Element blockElem, Map<String, String> query) throws IOException {
		LuceneMixBlock result = new LuceneMixBlock();
		String coefficients = LuceneUtils.getTextContentOfElement(blockElem, "coefficients", false);
		if (coefficients != null) {
			List<Double> coeffs = parseCoeff(coefficients);
			result.setCoefficients(coeffs);
		} else {
			result.setCoefficientsUniformly();
		}
		return result;
	}

	private static List<Double> parseCoeff(String coefficients) {
		String[] coefficientStrs = coefficients.split("\\s*[,\\s]\\s*");
		List<Double> coeffs = new ArrayList<Double>();
		for (String coef : coefficientStrs)
			coeffs.add(Double.parseDouble(coef));
		return coeffs;
	}

}
